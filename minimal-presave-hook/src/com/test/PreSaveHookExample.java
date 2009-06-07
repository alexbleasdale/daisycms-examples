package com.test;

import javax.annotation.PreDestroy;

import org.outerj.daisy.plugin.PluginRegistry;
import org.outerj.daisy.repository.Document;
import org.outerj.daisy.repository.Repository;
import org.outerj.daisy.repository.spi.local.PreSaveHook;

public class PreSaveHookExample implements PreSaveHook {

	private final PluginRegistry pluginRegistry;
	private final static String NAME = "example-presave-hook";

	public PreSaveHookExample(PluginRegistry pr) {
		System.out.println("Registering presave-hook example..");
		pluginRegistry = pr;
		pluginRegistry.addPlugin(PreSaveHook.class, NAME, this);
	}

	@Override
	public void process(Document doc, Repository repo) throws Exception {
		System.out.println("PreSaveHookExample - processing : "
				+ doc.getDocumentName());
	}

	@PreDestroy
	public void destroy() {
		pluginRegistry.removePlugin(PreSaveHook.class, NAME, this);
	}
}
