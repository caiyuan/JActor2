package org.agilewiki.jactor.util.osgi;

import org.agilewiki.jactor.util.durable.FactoryLocator;
import org.agilewiki.jactor.utilImpl.durable.FactoryLocatorImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;

public class FactoryLocatorActivator extends MailboxFactoryActivator {
    private FactoryLocatorImpl factoryLocator;

    @Override
    public void start(final BundleContext _bundleContext) throws Exception {
        setBundleContext(_bundleContext);
        factoryLocatorStart();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        setClosing();
    }

    protected final FactoryLocator getFactoryLocator() {
        return factoryLocator;
    }

    protected final void factoryLocatorStart() throws Exception {
        createFactoryLocator();
        Hashtable<String, String> p = new Hashtable<String, String>();
        p.put("bundleName", factoryLocator.getBundleName());
        p.put("bundleVersion", factoryLocator.getVersion());
        bundleContext.registerService(FactoryLocator.class, factoryLocator, p);
    }

    protected void createFactoryLocator() throws Exception {
        factoryLocator = new FactoryLocatorImpl();
        Bundle bundle = bundleContext.getBundle();
        factoryLocator.configure(
                bundle.getSymbolicName(),
                bundle.getVersion().toString(),
                bundle.getLocation());
    }
}
