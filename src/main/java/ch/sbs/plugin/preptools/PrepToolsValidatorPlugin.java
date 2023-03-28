// ATTENTION: generated file. Do not edit it, edit the build.xml instead.
package ch.sbs.plugin.preptools;

import ro.sync.exml.plugin.Plugin;
import ro.sync.exml.plugin.PluginDescriptor;

public class PrepToolsValidatorPlugin extends Plugin {

    private static PrepToolsValidatorPlugin instance = null;

    public PrepToolsValidatorPlugin(PluginDescriptor descriptor) {
        super(descriptor);

        if (instance != null) {
            throw new IllegalStateException("Already instantiated!");
        }
        instance = this;
    }

    public static PrepToolsValidatorPlugin getInstance() {
        return instance;
    }
}
