package org.agilewiki.jactor.util.osgi;

import org.agilewiki.jactor.api.*;
import org.agilewiki.jactor.impl.DefaultMailboxFactoryImpl;
import org.agilewiki.jactor.util.JAProperties;
import org.osgi.framework.*;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * A basic activator with a MailboxFactory,
 * with a reference to the BundleContext stored in the bundleContext property
 * in the MailboxFactory.
 */
abstract public class MailboxFactoryActivator
        extends ActorBase implements BundleActivator, ManagedService, AutoCloseable {

    /**
     * Returns the BundleContext saved in the bundleContext property of a MailboxFactory.
     *
     * @param _mailboxFactory The mailbox factory.
     * @return The BundleContext.
     */
    public static BundleContext getBundleContext(final MailboxFactory _mailboxFactory) {
        Properties p = _mailboxFactory.getProperties();
        return (BundleContext) p.getProperty("bundleContext");
    }

    /**
     * Returns the version in the form major.minor.micro or major.minor.micro-qualifier.
     * This is in contrast to Version.toString, which uses a . rather than a - with a qualifier.
     *
     * @param version The version.
     * @return The formatted version.
     */
    public static String getNiceVersion(Version version) {
        int q = version.getQualifier().length();
        StringBuffer result = new StringBuffer(20 + q);
        result.append(version.getMajor());
        result.append(".");
        result.append(version.getMinor());
        result.append(".");
        result.append(version.getMicro());
        if (q > 0) {
            result.append("-");
            result.append(version.getQualifier());
        }
        return result.toString();
    }

    /**
     * The version of the bundle.
     */
    private Version version;

    /**
     * Config properties, or null.
     */
    private Dictionary<String, ?> config;

    /**
     * The mailbox factory used by the bundle.
     */
    private MailboxFactory mailboxFactory;

    /**
     * The properties held by the mailbox factory.
     */
    private JAProperties jaProperties;

    /**
     * The bundle context.
     */
    protected BundleContext bundleContext;

    /**
     * True when the bundle is closed or closing.
     */
    private boolean closing;

    @Override
    public void start(final BundleContext _bundleContext) throws Exception {
        initializeActivator(_bundleContext);
        mailboxFactoryStart();
        beginReq().signal();
    }

    /**
     * Returns the request used to begin async processing.
     *
     * @return The request.
     */
    protected Request<Void> beginReq() {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(final Transport<Void> _transport) throws Exception {
                begin(_transport);
            }
        };
    }

    /**
     * Begins the activator's asynchronous processing.
     *
     * @param _transport The transport.
     */
    protected void begin(final Transport<Void> _transport) throws Exception {
        managedServiceRegistration();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        setClosing();
        mailboxFactory.close();
    }

    /**
     * Initialize the activator.
     *
     * @param _bundleContext The bundle context.
     */
    protected final void initializeActivator(final BundleContext _bundleContext) {
        bundleContext = _bundleContext;
        version = bundleContext.getBundle().getVersion();
    }

    /**
     * Returns the mailbox factory used by the bundle.
     *
     * @return The mailbox factory.
     */
    protected MailboxFactory getMailboxFactory() {
        return mailboxFactory;
    }

    /**
     * Create and initialize the mailbox factory.
     * The Properties object of the mailbox factory is created
     * and a bundleContext is added to it.
     * The activator is also added to the close set of the mailbox factory
     * and the activator is given a mailbox that may block.
     */
    protected final void mailboxFactoryStart() throws Exception {
        mailboxFactory = new DefaultMailboxFactoryImpl();
        mailboxFactory.addAutoClosable(this);
        jaProperties = new JAProperties(mailboxFactory, null);
        jaProperties.putProperty("bundleContext", bundleContext);
        initialize(mailboxFactory.createMailbox(true));
    }

    /**
     * Returns true when closing or closed.
     *
     * @return True when closing or closed.
     */
    protected final boolean isBundleClosing() {
        return closing;
    }

    /**
     * Mark as closing.
     */
    protected final void setClosing() {
        closing = true;
    }

    /**
     * Stop the bundle unless already closing.
     */
    @Override
    public void close() throws Exception {
        if (closing)
            return;
        Bundle bundle = bundleContext.getBundle();
        bundle.stop(Bundle.STOP_TRANSIENT);
    }

    /**
     * Returns the bundle version.
     *
     * @return The bundle version.
     */
    protected Version getVersion() {
        return version;
    }

    protected String getNiceVersion() {
        return getNiceVersion(getVersion());
    }

    /**
     * Register the activator as a managed service.
     */
    protected void managedServiceRegistration() {
        Hashtable<String, String> mp = new Hashtable<String, String>();
        mp.put(Constants.SERVICE_PID, this.getClass().getName() + "." + getVersion().toString());
        ServiceRegistration msr = bundleContext.registerService(
                ManagedService.class.getName(),
                this,
                mp);
    }

    @Override
    public void updated(final Dictionary<String, ?> _config) throws ConfigurationException {
        Dictionary<String, ?> oldConfig = config;
        config = _config;
        if (oldConfig == null && config != null)
            configInitialized();
    }

    protected void configInitialized() {

    }

    protected Dictionary<String, ?> getConfig() {
        return config;
    }
}
