// ATTENTION: generated file. Do not edit it, edit the build.xml instead.
package ch.sbs.plugin.preptools;

import ro.sync.exml.plugin.Plugin;
import ro.sync.exml.plugin.PluginDescriptor;

public class PrepToolsPlugin extends Plugin {

    private static PrepToolsPlugin instance = null;

    public PrepToolsPlugin(PluginDescriptor descriptor) {
	super(descriptor);

	if (instance != null) {
	    throw new IllegalStateException("Already instantiated!");
	}
	instance = this;
    }

    public static PrepToolsPlugin getInstance() {
	return instance;
    }
}
