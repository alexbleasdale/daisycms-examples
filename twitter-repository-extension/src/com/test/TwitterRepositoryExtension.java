package com.test;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.outerj.daisy.plugin.PluginRegistry;
import org.outerj.daisy.repository.Credentials;
import org.outerj.daisy.repository.Repository;
import org.outerj.daisy.repository.RepositoryEventType;
import org.outerj.daisy.repository.RepositoryListener;
import org.outerj.daisy.repository.RepositoryManager;
import org.outerj.daisy.repository.VariantKey;
import org.outerj.daisy.repository.spi.ExtensionProvider;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TwitterRepositoryExtension implements ExtensionProvider {

	PluginRegistry pluginRegistry;
	RepositoryManager repositoryManager;
	Twitter twitter;

	private Repository repository;
	private final String repoUser;
	private final String repoPassword;

	public TwitterRepositoryExtension(Configuration conf,
			PluginRegistry plugins, RepositoryManager repoMan) throws Exception {
		System.out.println("Registering TwitterRepositoryExtension");

		pluginRegistry = plugins;
		repositoryManager = repoMan;

		twitter = new Twitter(conf.getChild("twitterUser").getValue(), conf
				.getChild("twitterPasswd").getValue());

		System.out.println("Updates are being sent to: http://twitter.com/"
				+ conf.getChild("twitterUser").getValue());

		Configuration repositoryUserConf = conf.getChild("repositoryUser",
				false);

		if (repositoryUserConf == null)
			throw new ConfigurationException(
					"Missing repositoryUser configuration element.");

		// note with daisy 2.2 the attribute for the repository login-name is
		// "user"
		repoUser = repositoryUserConf.getAttribute("login");
		repoPassword = repositoryUserConf.getAttribute("password");

		repository = connectToRepository();

		attachRepositoryListener();
	}

	/**
	 * Attach listener and configure the Event rules.
	 */
	private void attachRepositoryListener() {
		repository.addListener(new RepositoryListener() {

			@Override
			public void repositoryEvent(RepositoryEventType evt, Object obj,
					long arg2) {

				/** Variant events */
				if (evt.isVariantEvent()) {
					VariantKey vk = (VariantKey) obj;

					/**
					 * handle variant creation
					 */
					if (RepositoryEventType.DOCUMENT_VARIANT_CREATED
							.equals(evt)) {
						try {
							twitter.updateStatus(vk.getDocumentId()
									+ " has just been created");
						} catch (TwitterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						System.out.println("** VARIANT HAS BEEN CREATED "
								+ vk.getDocumentId());
					}

					/**
					 * handle variant updates
					 */
					if (RepositoryEventType.DOCUMENT_VARIANT_UPDATED
							.equals(evt)) {
						try {
							twitter.updateStatus(vk.getDocumentId()
									+ " has just been updated");
						} catch (TwitterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("** VARIANT HAS BEEN UPDATED "
								+ vk.getDocumentId());
					}

					/**
					 * handle variant deletion
					 */
					if (RepositoryEventType.DOCUMENT_VARIANT_DELETED
							.equals(evt)) {
						try {
							twitter.updateStatus(vk.getDocumentId()
									+ " has just been deleted");
						} catch (TwitterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("** VARIANT HAS BEEN REMOVED "
								+ vk.getDocumentId());
					}

				}

			}
		});
	}

	/**
	 * Connect to repository.
	 * 
	 * @return the repository
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private Repository connectToRepository() throws Exception {
		try {
			repository = repositoryManager.getRepository(new Credentials(
					repoUser, repoPassword));
		} catch (Throwable e) {
			throw new Exception("Problem getting repository.", e);
		}
		return repository;
	}

	@Override
	public Object createExtension(Repository arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
