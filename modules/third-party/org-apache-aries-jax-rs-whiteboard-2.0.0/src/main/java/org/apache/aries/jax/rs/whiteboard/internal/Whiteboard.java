/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.aries.jax.rs.whiteboard.internal;

import org.apache.aries.jax.rs.whiteboard.internal.cxf.CxfJaxrsServiceRegistrator;
import org.apache.aries.jax.rs.whiteboard.internal.cxf.PrototypeServiceReferenceResourceProvider;
import org.apache.aries.jax.rs.whiteboard.internal.cxf.SingletonServiceReferenceResourceProvider;
import org.apache.aries.jax.rs.whiteboard.internal.utils.Utils;
import org.apache.aries.jax.rs.whiteboard.internal.utils.PropertyHolder;
import org.apache.aries.jax.rs.whiteboard.internal.utils.ServiceTuple;
import org.apache.aries.component.dsl.CachingServiceReference;
import org.apache.aries.component.dsl.OSGi;
import org.apache.aries.component.dsl.OSGiResult;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.extension.ExtensionManagerBus;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.osgi.framework.*;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.jaxrs.runtime.JaxrsServiceRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;
import static org.apache.aries.jax.rs.whiteboard.internal.AriesJaxrsServiceRuntime.getServiceName;
import static org.apache.aries.jax.rs.whiteboard.internal.utils.LogUtils.ifDebugEnabled;
import static org.apache.aries.jax.rs.whiteboard.internal.utils.LogUtils.ifErrorEnabled;
import static org.apache.aries.jax.rs.whiteboard.internal.utils.Utils.canonicalize;
import static org.apache.aries.jax.rs.whiteboard.internal.utils.Utils.canonicalizeAddress;
import static org.apache.aries.jax.rs.whiteboard.internal.utils.Utils.getApplicationProperties;
import static org.apache.aries.jax.rs.whiteboard.internal.utils.Utils.getString;
import static org.apache.aries.jax.rs.whiteboard.internal.utils.Utils.highestPer;
import static org.apache.aries.jax.rs.whiteboard.internal.utils.Utils.mergePropertyMaps;
import static org.apache.aries.jax.rs.whiteboard.internal.utils.Utils.onlyGettables;
import static org.apache.aries.jax.rs.whiteboard.internal.utils.Utils.updateProperty;
import static org.apache.aries.component.dsl.OSGi.NOOP;
import static org.apache.aries.component.dsl.OSGi.all;
import static org.apache.aries.component.dsl.OSGi.changeContext;
import static org.apache.aries.component.dsl.OSGi.effects;
import static org.apache.aries.component.dsl.OSGi.ignore;
import static org.apache.aries.component.dsl.OSGi.just;
import static org.apache.aries.component.dsl.OSGi.nothing;
import static org.apache.aries.component.dsl.OSGi.once;
import static org.apache.aries.component.dsl.OSGi.register;
import static org.apache.aries.component.dsl.OSGi.serviceReferences;
import static org.apache.aries.component.dsl.Utils.accumulateInMap;
import static org.apache.aries.component.dsl.Utils.highest;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET;
import static org.osgi.service.jaxrs.runtime.JaxrsServiceRuntimeConstants.JAX_RS_SERVICE_ENDPOINT;
import static org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE;
import static org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SELECT;
import static org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants.JAX_RS_DEFAULT_APPLICATION;
import static org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants.JAX_RS_EXTENSION;
import static org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants.JAX_RS_EXTENSION_SELECT;
import static org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants.JAX_RS_NAME;
import static org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants.JAX_RS_RESOURCE;
import static org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants.JAX_RS_WHITEBOARD_TARGET;

/**
 * @author Carlos Sierra Andrés
 */
public class Whiteboard {

    public static final Map<String, Class<?>> SUPPORTED_EXTENSION_INTERFACES =
        Collections.unmodifiableMap(
            Stream.of(ContainerRequestFilter.class,
                ContainerResponseFilter.class,
                ReaderInterceptor.class,
                WriterInterceptor.class,
                MessageBodyReader.class,
                MessageBodyWriter.class,
                ContextResolver.class,
                ExceptionMapper.class,
                ParamConverterProvider.class,
                Feature.class,
                DynamicFeature.class,
                org.apache.cxf.feature.Feature.class,
                org.apache.cxf.jaxrs.ext.ContextProvider.class)
            .collect(toMap(Class::getName, Function.identity())));

    static final String DEFAULT_NAME = ".default";

    private static final Logger _log = LoggerFactory.getLogger(
        Whiteboard.class);
    private final String _applicationBasePrefix;
    private final Registry<CxfJaxrsServiceRegistrator> _applicationRegistry =
        new Registry<>();

    private final AriesJaxrsServiceRuntime _runtime;
    private final Map<String, ?> _configurationMap;
    private volatile BundleContext _bundleContext;
    private volatile ServiceRegistrationChangeCounter _counter;
    private volatile ServiceReference<JaxrsServiceRuntime> _runtimeReference;
    private final OSGi<Void> _program;
    private final List<Object> _endpoints = new ArrayList<>();
    private volatile ServiceRegistration<JaxrsServiceRuntime>
        _runtimeRegistration;
    private OSGiResult _osgiResult;

    private Whiteboard(Dictionary<String, ?> configuration) {
        _runtime = new AriesJaxrsServiceRuntime(this);
        _configurationMap = Maps.from(configuration);

        _applicationBasePrefix = canonicalizeAddress(
            getString(_configurationMap.get("application.base.prefix")));

        _program =
            all(
                ignore(registerDefaultApplication()),
                ignore(getAllServices())
            );
    }

    public static Whiteboard createWhiteboard(
        Dictionary<String, ?> configuration) {

        return new Whiteboard(configuration);
    }

    public void start(BundleContext bundleContext) {
        _bundleContext = bundleContext;
        _runtimeRegistration = registerJaxRSServiceRuntime(
            new HashMap<>(_configurationMap));
        _runtimeReference = _runtimeRegistration.getReference();
        _counter = new ServiceRegistrationChangeCounter(_runtimeRegistration);
        _osgiResult = _program.run(bundleContext);
    }

    public void stop() {
        _osgiResult.close();
        _runtimeRegistration.unregister();
        _applicationRegistry.close();
    }

    public void addHttpEndpoints(List<String> endpoints) {
        synchronized (_runtimeRegistration) {
            _endpoints.addAll(endpoints);

            updateProperty(
                _runtimeRegistration, JAX_RS_SERVICE_ENDPOINT, _endpoints);
        }
    }

    public ServiceReference<JaxrsServiceRuntime> getServiceReference() {
        return _runtimeReference;
    }

    private OSGi<?> applicationExtensions(
        OSGi<CachingServiceReference<Object>> extensions) {

        return
            onlyValid(
                onlySupportedInterfaces(
                        extensions,
                        _runtime::addInvalidExtension,
                        _runtime::removeInvalidExtension),
                    _runtime::addInvalidExtension,
                    _runtime::removeInvalidExtension).
                flatMap(extensionReference ->
            chooseApplication(
                    extensionReference,
                    _runtime::addApplicationDependentExtension,
                    _runtime::removeApplicationDependentExtension).
                flatMap(registrator ->
            waitForExtensionDependencies(
                    extensionReference,
                    registrator,
                    er ->
                        _runtime.addDependentExtensionInApplication(
                            registrator.getProperties(), er),
                    er ->
                        _runtime.removeDependentExtensionFromApplication(
                            registrator.getProperties(), er)).
                then(
            safeRegisterExtension(extensionReference, registrator)
        )));
    }

    private OSGi<?> applicationResources(
        OSGi<CachingServiceReference<Object>> resources) {

        return
            onlyValid(
                    resources, _runtime::addInvalidResource,
                    _runtime::removeInvalidResource).
                flatMap(resourceReference ->
            chooseApplication(
                    resourceReference,
                    _runtime::addApplicationDependentResource,
                    _runtime::removeApplicationDependentResource).
                flatMap(registrator ->
            waitForExtensionDependencies(
                    resourceReference, registrator,
                    _runtime::addDependentService,
                    _runtime::removeDependentService).
            then(
                safeRegisterEndpoint(resourceReference, registrator)
            )));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private OSGi<?> getAllServices() {
        OSGi<CachingServiceReference<Object>> applicationsForWhiteboard =
            (OSGi)getApplicationsForWhiteboard();
        return
            highestPer(
                sr -> just(getServiceName(sr::getProperty)),
                countChanges(
                    all(
                        getResourcesForWhiteboard(),
                        getApplicationExtensionsForWhiteboard(),
                        applicationsForWhiteboard
                    )
                ),
                this::registerShadowedService,
                this::unregisterShadowedService
            ).
            effects(
                _runtime::addServiceForName,
                _runtime::removedServiceForName
            ).
            distribute(
                p -> ignore(applications((OSGi)p.filter(this::isApplication))),
                p -> ignore(applicationResources(p.filter(this::isResource))),
                p -> ignore(applicationExtensions(p.filter(this::isExtension)))
            );
    }

    private boolean isApplication(CachingServiceReference<?> sr) {
        return _applicationsFilter.match(sr.getServiceReference());
    }

    private boolean isExtension(CachingServiceReference<?> sr) {
        return _extensionsFilter.match(sr.getServiceReference());
    }

    private boolean isResource(CachingServiceReference<?> sr) {
        return _resourcesFilter.match(sr.getServiceReference());
    }

    private <T> boolean matchesWhiteboard(CachingServiceReference<T> ref) {
        String target = (String)ref.getProperty(JAX_RS_WHITEBOARD_TARGET);

        if (target == null) {
            return true;
        }

        Filter filter;

        try {
            filter = FrameworkUtil.createFilter(target);
        }
        catch (InvalidSyntaxException ise) {
            if (_log.isErrorEnabled()) {
                _log.error(
                    "Invalid '{}' filter syntax in {}",
                    JAX_RS_WHITEBOARD_TARGET, ref);
            }

            return false;
        }

        return filter.match(_runtimeReference);
    }

    private void registerShadowedService(CachingServiceReference<?> sr) {
        if (isApplication(sr)) {
            _runtime.addClashingApplication(sr);
        }
        if (isExtension(sr)) {
            _runtime.addClashingExtension(sr);
        }
        if (isResource(sr)) {
            _runtime.addClashingResource(sr);
        }
    }

    private void unregisterShadowedService(CachingServiceReference<?> sr) {
        if (isApplication(sr)) {
            _runtime.removeClashingApplication(sr);
        }
        if (isExtension(sr)) {
            _runtime.removeClashingExtension(sr);
        }
        if (isResource(sr)) {
            _runtime.removeClashingResource(sr);
        }
    }

    private static <T> OSGi<? extends ResourceProvider> getResourceProvider(
        ServiceTuple<T> serviceTuple) {

        CachingServiceReference<T> cachingServiceReference =
            serviceTuple.getCachingServiceReference();

        String scope = Utils.getString(cachingServiceReference.getProperty("service.scope"));

        if (Objects.equals(scope, "prototype")) {
            boolean applicationScoped =
                Boolean.parseBoolean(
                    getString(
                        cachingServiceReference.getProperty(
                            "org.apache.aries.jax.rs.whiteboard.application.scoped")));

            if (applicationScoped) {
                return just(
                    serviceTuple.getServiceObjects().getService()
                ).effects(
                    __ -> {},
                    serviceTuple.getServiceObjects()::ungetService
                ).map(
                    service -> new SingletonServiceReferenceResourceProvider(
                        cachingServiceReference, service)
                );
            }
            else {
                return just(
                    new PrototypeServiceReferenceResourceProvider(
                        cachingServiceReference,
                        serviceTuple.getService().getClass(),
                        serviceTuple.getServiceObjects())
                ).effects(
                    __ -> {},
                    __ -> serviceTuple.dispose(),
                    __ -> {},
                    __ -> {}
                );
            }
        }
        else {
            return just(
                new SingletonServiceReferenceResourceProvider(
                    serviceTuple.getCachingServiceReference(),
                    serviceTuple.getService()
            ));
        }
    }

    private static <T> OSGi<CachingServiceReference<T>> onlyValid(
        OSGi<CachingServiceReference<T>> serviceReferences,
        Consumer<CachingServiceReference<T>> onAddingInvalid,
        Consumer<CachingServiceReference<T>> onRemovingInvalid) {

        return serviceReferences.flatMap(serviceReference -> {

            OSGi<CachingServiceReference<T>> error = effects(
                () -> onAddingInvalid.accept(serviceReference),
                () -> onRemovingInvalid.accept(serviceReference)
            ).
            effects(
                ifDebugEnabled(_log, () -> "Invalid service {}"),
                ifDebugEnabled(_log, () -> "Invalid service {} is gone")
            ).
            then(
                nothing()
            );

            Object propertyObject = serviceReference.getProperty(JAX_RS_NAME);

            if (propertyObject != null &&
                !propertyObject.toString().equals(JAX_RS_DEFAULT_APPLICATION) &&
                propertyObject.toString().startsWith(".")) {

                if (_log.isWarnEnabled()) {
                    _log.warn(
                        "Invalid property {} in service {}",
                        JAX_RS_DEFAULT_APPLICATION, serviceReference);
                }

                return error;
            }

            if (!testFilters(
                serviceReference.getProperty(JAX_RS_APPLICATION_SELECT))) {

                if (_log.isWarnEnabled()) {
                    _log.warn(
                        "Invalid value for property {} in service {}",
                        JAX_RS_APPLICATION_SELECT, serviceReference);
                }

                return error;
            }

            if (!testFilters(
                serviceReference.getProperty(JAX_RS_EXTENSION_SELECT))) {

                if (_log.isWarnEnabled()) {
                    _log.warn(
                        "Invalid value for property {} in service {}",
                        JAX_RS_EXTENSION_SELECT, serviceReference);
                }

                return error;
            }

            return just(serviceReference);
        });
    }

    private static boolean testFilters(Object propertyObject) {
        if (propertyObject != null) {
            try {
                String[] properties = canonicalize(propertyObject);

                for (String property : properties) {
                    FrameworkUtil.createFilter(property);
                }
            }
            catch (InvalidSyntaxException e) {
                return false;
            }
        }
        return true;
    }

    private OSGi<ApplicationReferenceWithContext> waitForApplicationContext(
        OSGi<CachingServiceReference<Application>> application,
        Consumer<CachingServiceReference<Application>> onWaitingForContext,
        Consumer<CachingServiceReference<Application>> onResolvedContext) {

        return application.flatMap(serviceReference -> {
            Object propertyObject = serviceReference.getProperty(
                HTTP_WHITEBOARD_CONTEXT_SELECT);

            if (propertyObject == null) {
                propertyObject = _configurationMap.get(
                    HTTP_WHITEBOARD_CONTEXT_SELECT);

                if (propertyObject == null) {
                    return just(
                        new ApplicationReferenceWithContext(
                            null, serviceReference));
                }
            }

            String contextSelect = propertyObject.toString();

            try {
                FrameworkUtil.createFilter(contextSelect);
            }
            catch (InvalidSyntaxException e) {
                return effects(
                    () -> _runtime.addInvalidApplication(serviceReference),
                    () -> _runtime.removeInvalidApplication(serviceReference)
                ).then(
                    nothing()
                );
            }

            return
                effects(
                    () -> onWaitingForContext.accept(serviceReference),
                    () -> onResolvedContext.accept(serviceReference)
                ).then(
            highest(
                serviceReferences(ServletContextHelper.class, contextSelect)
            ).flatMap(
                sr -> just(
                    new ApplicationReferenceWithContext(sr, serviceReference))
            ).effects(
                __ -> onResolvedContext.accept(serviceReference),
                __ -> onWaitingForContext.accept(serviceReference)
            ));
        });
    }

    private OSGi<?> applications(
        OSGi<CachingServiceReference<Application>> applications) {

        OSGi<CachingServiceReference<Application>> validApplications =
            onlyValid(
                applications,
                _runtime::addInvalidApplication,
                _runtime::removeInvalidApplication);

        OSGi<ApplicationReferenceWithContext> applicationsWithContext =
            waitForApplicationContext(
                validApplications,
                _runtime::addContextDependentApplication,
                _runtime::removeContextDependentApplication);

        OSGi<ApplicationReferenceWithContext> highestRankedPerPath =
            highestPer(
                arwc -> just(arwc.getActualBasePath()),
                applicationsWithContext,
                t -> _runtime.addShadowedApplication(
                    t.getApplicationReference(), t.getActualBasePath()),
                t -> _runtime.removeShadowedApplication(
                    t.getApplicationReference())
        );

        return highestRankedPerPath.flatMap(application ->
            onlyGettables(
                waitForReadyService(
                    just(application.getApplicationReference())),
                _runtime::addNotGettableApplication,
                _runtime::removeNotGettableApplication, _log).
            recoverWith(
                (t, e) ->
                    just(t).map(
                        ServiceTuple::getCachingServiceReference
                    ).effects(
                        _runtime::addErroredApplication,
                        _runtime::removeErroredApplication
                    ).then(
                        nothing()
                    )
                ).
                effects(
                    __ -> {},
                    this::clearApplicationState
            ).flatMap(at ->
            deployApplication(at, application.getContextReference()).foreach(
                registrator ->
                    _runtime.setApplicationForPath(
                        getApplicationBase(
                            at.getCachingServiceReference()::getProperty),
                        at.getCachingServiceReference(),
                        registrator),
                registrator ->
                    _runtime.unsetApplicationForPath(
                        getApplicationBase(
                            at.getCachingServiceReference()::getProperty))
                )
        ));
    }

    private ExtensionManagerBus createBus(
        Map<String, ServiceTuple<Object>> extensions) {

        BundleWiring wiring = _bundleContext.getBundle().adapt(
            BundleWiring.class);

        Map<String, Object> properties = new HashMap<>(_configurationMap);
        properties.putIfAbsent(
            "replace.loopback.address.with.localhost", "false");

        HashMap<Class<?>, Object> cxfExtensions = new HashMap<>();

        if (extensions.isEmpty()) {
            cxfExtensions = null;
        }
        else {
            for (Map.Entry<String, ServiceTuple<Object>> entry :
                extensions.entrySet()) {

                String className = entry.getKey();

                ServiceTuple<Object> serviceTuple = entry.getValue();

                ClassLoader classLoader = getClassLoader(serviceTuple);

                try {
                    Class<?> clazz = classLoader.loadClass(className);

                    cxfExtensions.put(clazz, serviceTuple.getService());
                }
                catch (Exception e) {
                    if (_log.isErrorEnabled()) {
                        _log.error("Could not load extension for CXF bus", e);
                    }
                }
            }
        }

        if (_log.isDebugEnabled()) {
            _log.debug(
                "Creating CXF Bus with extensions {} and properties {}",
                extensions, properties);
        }

        ExtensionManagerBus bus = new ExtensionManagerBus(
            cxfExtensions, properties, wiring.getClassLoader());

        bus.initialize();

        if (_log.isDebugEnabled()) {
            _log.debug(
                "Created CXF Bus with extensions {} and properties {}",
                extensions, properties);
        }

        return bus;
    }

    private void clearApplicationState(ServiceTuple<Application> tuple) {
        CachingServiceReference<?>
            cachingServiceReference = tuple.getCachingServiceReference();

        _runtime.unregisterApplicationExtensions(cachingServiceReference);
    }

    private OSGi<CxfJaxrsServiceRegistrator> applicationMatching(
        String filter) {

        return _applicationRegistry.waitForService(filter);
    }

    private OSGi<CxfJaxrsServiceRegistrator> deployApplication(
        ServiceTuple<Application> tuple,
        CachingServiceReference<ServletContextHelper> contextReference) {

        CachingServiceReference<Application> serviceReference =
            tuple.getCachingServiceReference();

        Map<String, Object> properties = getApplicationProperties(
            serviceReference);

        properties.put(
            "original.service.id",
            serviceReference.getProperty("service.id"));

        properties.put(
            "original.service.bundleid",
            serviceReference.getProperty("service.bundleid"));

        return
            getCxfExtensions(tuple.getCachingServiceReference()).
                flatMap(extensions ->
            createRegistrator(extensions, tuple, properties).
                flatMap(registrator ->
            waitForApplicationDependencies(
                tuple.getCachingServiceReference(),
                _applicationRegistry.registerService(
                    registrator, registrator.getProperties())).
                then(
            registerCXFServletService(
                    registrator.getBus(), properties, contextReference).
                then(
            just(registrator)
        ))));
    }

    public OSGi<Map<String, ServiceTuple<Object>>> getCxfExtensions(
        CachingServiceReference<Application> applicationReference) {

        OSGi<ServiceTuple<Object>> cxfExtensionsForApplication =
            onlyGettables(
                serviceReferences("(cxf.extension=true)").filter(
                    sr -> {
                        Object appFilter = sr.getProperty(
                            JAX_RS_APPLICATION_SELECT);

                        if (appFilter == null) {
                            return true;
                        }
                        else {
                            try {
                                Filter filter = FrameworkUtil.createFilter(
                                    appFilter.toString());

                                return filter.match(
                                    applicationReference.getServiceReference());
                            }
                            catch (InvalidSyntaxException e) {
                                return false;
                            }
                        }
                    }
                ).filter(this::matchesWhiteboard),
                __ -> {}, __ -> {}, _log);

        return accumulateInMap(
            cxfExtensionsForApplication,
            st -> just(
                Arrays.asList(
                    canonicalize(
                        st.getCachingServiceReference().
                            getProperty("objectClass")))),
            OSGi::just);
    }

    private ClassLoader getClassLoader(ServiceTuple<?> serviceTuple) {
        return serviceTuple.
            getCachingServiceReference().
            getServiceReference().
            getBundle().
            adapt(BundleWiring.class).
            getClassLoader();
    }

    private OSGi<CxfJaxrsServiceRegistrator> createRegistrator(
        Map<String, ServiceTuple<Object>> extensions,
        ServiceTuple<Application> tuple, Map<String, Object> props) {

        return
            just(() -> new CxfJaxrsServiceRegistrator(
                    createBus(extensions), tuple, props, _runtime)).
            effects(
                __ -> {},
                CxfJaxrsServiceRegistrator::enable,
                CxfJaxrsServiceRegistrator::close,
                __ -> {});
    }

    private OSGi<CachingServiceReference<Object>>
        getApplicationExtensionsForWhiteboard() {

        return serviceReferences(_extensionsFilter.toString()).
            filter(this::matchesWhiteboard);
    }

    private OSGi<CachingServiceReference<Application>>
        getApplicationsForWhiteboard() {

        return
            serviceReferences(
                    Application.class, _applicationsFilter.toString()).
                filter(this::matchesWhiteboard);
    }

    private String getApplicationReadyServiceFilter(
        CachingServiceReference<Application> reference) {

        Object applicationReadyServiceFilter =
            _configurationMap.get("application.ready.service.filter");

        if (applicationReadyServiceFilter == null) {
            applicationReadyServiceFilter =
                reference.getProperty("application.ready.service.filter");
        }

        if (applicationReadyServiceFilter != null) {
            return String.valueOf(applicationReadyServiceFilter);
        }

        return null;
    }

    private OSGi<CachingServiceReference<Object>> getResourcesForWhiteboard() {
        return serviceReferences(_resourcesFilter.toString()).
            filter(this::matchesWhiteboard);
    }

    private OSGi<ServiceRegistration<Application>>
        registerDefaultApplication() {

        return OSGi.register(
            Application.class, DefaultApplication::new,
            () -> {
                Object defaultApplicationBase = _configurationMap.get(
                    "default.application.base");

                if (defaultApplicationBase == null ||
                    !(defaultApplicationBase instanceof String)) {

                    defaultApplicationBase = "/";
                }

                Map<String, Object> properties = new HashMap<>();

                mergePropertyMaps(properties, _configurationMap);

                properties.put(JAX_RS_NAME, DEFAULT_NAME);
                properties.put(JAX_RS_APPLICATION_BASE, defaultApplicationBase);
                properties.put("service.ranking", Integer.MIN_VALUE);
                properties.put(
                    JAX_RS_WHITEBOARD_TARGET,
                    "(" + SERVICE_PID + "=" + _configurationMap.get(SERVICE_PID)
                        + ")");

                return properties;
            });
    }

    private ServiceRegistration<JaxrsServiceRuntime>
        registerJaxRSServiceRuntime(Map<String, Object> properties) {

        properties.putIfAbsent(Constants.SERVICE_RANKING, Integer.MIN_VALUE);

        return _bundleContext.registerService(
            JaxrsServiceRuntime.class, _runtime, new Hashtable<>(properties));
    }

    public void removeHttpEndpoints(List<String> endpoints) {
        synchronized (_runtimeRegistration) {
            _endpoints.removeAll(endpoints);

            updateProperty(
                _runtimeRegistration, JAX_RS_SERVICE_ENDPOINT, _endpoints);
        }
    }

    private <T> OSGi<?> safeRegisterEndpoint(
        CachingServiceReference<T> serviceReference,
        CxfJaxrsServiceRegistrator registrator) {

        Map<?, ?> properties = registrator.getProperties();

        Bundle originalBundle = _bundleContext.getBundle(
            Long.class.cast(properties.get("service.bundleid")).longValue());

        return
            changeContext(
                originalBundle.getBundleContext(),
                onlyGettables(
                    just(serviceReference),
                    _runtime::addNotGettableEndpoint,
                    _runtime::removeNotGettableEndpoint,
                    _log
                )
            ).recoverWith((t, e) ->
                just(serviceReference).
                effects(
                    _runtime::addErroredEndpoint,
                    _runtime::removeErroredEndpoint).
                effects(
                    ifErrorEnabled(
                        _log,
                        () -> "ServiceReference {} for endpoint produced " +
                            "error: {}",
                        e),
                    ifErrorEnabled(
                        _log,
                        () -> "Errored ServiceReference {} for endpoint left")
                ).
                then(nothing())
            ).flatMap(
                Whiteboard::getResourceProvider
            ).effects(
                rp -> _runtime.addApplicationEndpoint(
                    properties::get, serviceReference,
                    registrator.getBus(), rp.getResourceClass()),
                rp -> _runtime.removeApplicationEndpoint(
                    properties::get, serviceReference)
            ).effects(
                registrator::add,
                registrator::remove
            ).effects(
                ifDebugEnabled(
                    _log,
                    () -> "Registered endpoint " +
                        serviceReference.getServiceReference() + " into application " +
                            getServiceName(properties::get)
                ),
                ifDebugEnabled(
                    _log,
                    () -> "Unregistered endpoint " +
                        serviceReference.getServiceReference() + " from application " +
                            getServiceName(properties::get)
                )

            );
    }

    private OSGi<?> safeRegisterExtension(
        CachingServiceReference<?> serviceReference,
        CxfJaxrsServiceRegistrator registrator) {

        Map<?, ?> properties = registrator.getProperties();

        Bundle originalBundle = _bundleContext.getBundle(
            Long.class.cast(properties.get("service.bundleid")).longValue());

        return
            just(() -> getServiceName(properties::get)).
                flatMap(applicationName ->
            changeContext(
                originalBundle.getBundleContext(),
                    onlyGettables(
                        just(serviceReference),
                        _runtime::addNotGettableExtension,
                        _runtime::removeNotGettableExtension,
                        _log
                    )
                ).recoverWith(
                (t, e) ->
                    just(t.getCachingServiceReference()).
                    effects(
                        _runtime::addErroredExtension,
                        _runtime::removeErroredExtension
                    ).
                    effects(
                        ifErrorEnabled(
                            _log,
                            () -> "ServiceReference {} for extension " +
                                "produced error: {}",
                            e),
                        ifErrorEnabled(
                            _log,
                            () -> "Errored ServiceReference {} for extension " +
                                "left")
                    ).
                    then(nothing())
            ).effects(
                registrator::addProvider,
                registrator::removeProvider
            ).effects(
                t -> _runtime.addApplicationExtension(
                    properties::get, serviceReference,
                    t.getService().getClass()),
                __ -> _runtime.removeApplicationExtension(
                    properties::get, serviceReference)
            ).
            effects(
                ifDebugEnabled(
                    _log,
                    () ->
                        "Registered extension " +
                            serviceReference.getServiceReference() +
                                " into application " +
                            getServiceName(properties::get)
                ),
                ifDebugEnabled(
                    _log,
                    () ->
                        "Unregistered extension  " +
                            serviceReference.getServiceReference() +
                            " from application " +
                            getServiceName(properties::get)
                )

            ).
            effects(
                __ -> registrator.registerExtension(
                    serviceReference),
                __ -> registrator.unregisterExtension(
                    serviceReference)
            ));
    }



    private OSGi<CxfJaxrsServiceRegistrator>
        waitForApplicationDependencies(
            CachingServiceReference<Application> applicationReference,
            OSGi<CxfJaxrsServiceRegistrator> registrators) {

        return registrators.flatMap(registrator -> {
            String[] extensionDependencies = canonicalize(
                applicationReference.getProperty(JAX_RS_EXTENSION_SELECT));

            OSGi<CxfJaxrsServiceRegistrator> program = just(registrator);

            if (extensionDependencies.length == 0) {
                return program;
            }

            for (String extensionDependency : extensionDependencies) {
                if  (_log.isDebugEnabled()) {
                    _log.debug(
                        "Application {} has a dependency on {}",
                        registrator, extensionDependency);
                }

                try {
                    Filter extensionFilter = _bundleContext.createFilter(
                        extensionDependency);

                    if (extensionFilter.match(_runtimeReference)) {
                        if(_log.isDebugEnabled()) {
                            _log.debug(
                                "Application dependency {} provided by " +
                                    "whiteboard {}",
                                extensionDependency, _runtimeReference);
                        }

                        continue;
                    }
                }
                catch (InvalidSyntaxException e) {
                    return program.
                        effects(
                            ifErrorEnabled(
                                _log,
                                () -> String.format(
                                    "Application %s has invalid dependency %s",
                                    registrator, extensionDependency)),
                            ifErrorEnabled(
                                _log,
                                () -> String.format(
                                    "Application %s with invalid dependency " +
                                        "has left",
                                    registrator))
                        ).
                        effects(
                            __ -> _runtime.addErroredApplication(
                                applicationReference),
                            __ -> _runtime.removeErroredApplication(
                                applicationReference)
                        ).then(
                            nothing()
                        );
                }

                program =
                    once(
                        registrator.waitForExtension(extensionDependency).
                        flatMap(
                            sr -> {
                                Object applicationSelectProperty =
                                    sr.getProperty(JAX_RS_APPLICATION_SELECT);

                                if (applicationSelectProperty == null) {
                                    return just(registrator);
                                }

                                Filter filter;

                                try {
                                    filter = _bundleContext.createFilter(
                                        applicationSelectProperty.toString());
                                }
                                catch (InvalidSyntaxException e) {
                                    return nothing();
                                }

                                if (filter.match(
                                    applicationReference.
                                        getServiceReference())) {

                                    return just(registrator);
                                }

                                return nothing();
                            }
                        )).effects(
                                __ -> {},
                                __ -> _runtime.addDependentApplication(
                                    applicationReference)
                        ).
                        effects(
                            ifDebugEnabled(
                                _log,
                                () -> "Application "+ registrator +
                                    " dependency " + extensionDependency +
                                        " has been fullfiled"),
                            ifDebugEnabled(
                                _log,
                                () -> "Application "+ registrator +
                                    " dependency " + extensionDependency +
                                        " has gone")
                        ).
                    then(program);
            }

            program = effects(
                () -> _runtime.addDependentApplication(applicationReference),
                () -> _runtime.removeDependentApplication(applicationReference)
            ).then(program);

            program = program.effects(
                __ -> _runtime.removeDependentApplication(applicationReference),
                __ -> {}
            );

            return program;
        });
    }

    private OSGi<CachingServiceReference<Application>> waitForReadyService(
        OSGi<CachingServiceReference<Application>> program) {

        return program.flatMap(reference -> {
            String applicationReadyServiceFilter =
                getApplicationReadyServiceFilter(reference);

            if (applicationReadyServiceFilter != null) {
                return effects(
                    () -> _runtime.addDependentApplication(reference),
                    () -> _runtime.removeDependentApplication(reference)
                ).then(
                    once(serviceReferences(applicationReadyServiceFilter)).
                        effects(
                            ifDebugEnabled(
                                _log,
                                () ->
                                    "Ready service for " + reference +
                                        " has been tracked"),
                            ifDebugEnabled(
                                _log,
                                () -> "Ready service for " + reference +
                                    " is gone")
                        ).
                        then(just(reference).
                            effects(
                                __ -> {},
                                __ -> _runtime.addDependentApplication(
                                    reference)))
                ).effects(
                    _runtime::removeDependentApplication,
                    __ -> {}
                );
            }

            return just(reference);
        });
    }

    private OSGi<?> waitForExtensionDependencies(
        CachingServiceReference<?> reference,
        CxfJaxrsServiceRegistrator cxfJaxrsServiceRegistrator,
        Consumer<CachingServiceReference<?>> onAddingDependent,
        Consumer<CachingServiceReference<?>> onRemovingDependent) {

        Map<String, ?> applicationRegistratorProperties =
            cxfJaxrsServiceRegistrator.getProperties();

        String[] extensionDependencies = canonicalize(
            reference.getProperty(JAX_RS_EXTENSION_SELECT));

        OSGi<CachingServiceReference<?>> program = just(reference);

        if (extensionDependencies.length == 0) {
            return program;
        }

        for (String extensionDependency : extensionDependencies) {
            if  (_log.isDebugEnabled()) {
                _log.debug(
                    "Extension {} has a dependency on {}",
                    reference, extensionDependency);
            }

            try {
                String finalExtensionDependency = extensionDependency.replace(
                    "(objectClass=", "(original.objectClass=");

                Filter extensionFilter = _bundleContext.createFilter(
                    finalExtensionDependency);

                if (extensionFilter.match(_runtimeReference) ||
                    extensionFilter.matches(
                        applicationRegistratorProperties))
                {
                    continue;
                }

                program =
                    once(
                        cxfJaxrsServiceRegistrator.waitForExtension(
                            extensionDependency).
                        effects(
                            __ -> {},
                            __ -> onAddingDependent.accept(reference)
                        )
                    ).effects(
                        ifDebugEnabled(
                            _log,
                            () -> "Extension " + reference +
                                " dependency " + extensionDependency +
                                    " has been fullfiled"),
                        ifDebugEnabled(
                            _log,
                            () -> "Extension " + reference +
                                " dependency " + extensionDependency +
                                    " has gone")
                    ).
                then(program);
            }
            catch (InvalidSyntaxException e) {

            }
        }

        program = effects(
            () -> onAddingDependent.accept(reference),
            () -> onRemovingDependent.accept(reference)).
            then(program);

        program = program.effects(
            __ -> onRemovingDependent.accept(reference),
            __ -> {}
        );

        return program;
    }

    String getApplicationBase(PropertyHolder properties) {
        return _applicationBasePrefix + getString(
            properties.get(JAX_RS_APPLICATION_BASE));
    }

    private OSGi<CxfJaxrsServiceRegistrator>
        chooseApplication(
            CachingServiceReference<?> serviceReference,
            Consumer<CachingServiceReference<?>> onWaiting,
            Consumer<CachingServiceReference<?>> onResolved) {

        Object applicationSelectProperty = serviceReference.getProperty(
            JAX_RS_APPLICATION_SELECT);

        if (applicationSelectProperty == null) {
            return applicationMatching(
                String.format("(%s=%s)", JAX_RS_NAME, DEFAULT_NAME));
        }

        return
            just(AtomicInteger::new).flatMap(counter ->
            effects(
                () -> onWaiting.accept(serviceReference),
                () -> {
                    onResolved.accept(serviceReference);

                    counter.set(0);
                }).then(
            applicationMatching(applicationSelectProperty.toString()).
            effects(
                __ -> {
                    if (counter.getAndIncrement() == 0) {
                        onResolved.accept(serviceReference);
                    }
                },
                __ -> {
                    if (counter.decrementAndGet() == 0) {
                        onWaiting.accept(serviceReference);
                    }
                })
            ));
    }

    private <T> OSGi<T> countChanges(OSGi<T> program) {

        return program.effects(
            __ -> {},
            __ -> _counter.inc(),
            __ -> {},
            __ -> _counter.inc()
        );
    }

    private static CXFNonSpringServlet createCXFServlet(Bus bus) {
        @SuppressWarnings("serial")
        CXFNonSpringServlet cxfNonSpringServlet = new CXFNonSpringServlet() {

            @Override
            public void destroyBus() {
            }

        };

        cxfNonSpringServlet.setBus(bus);

        return cxfNonSpringServlet;
    }

    private static OSGi<CachingServiceReference<Object>>
        onlySupportedInterfaces(
            OSGi<CachingServiceReference<Object>> program,
            Consumer<CachingServiceReference<?>> onInvalidAdded,
            Consumer<CachingServiceReference<?>> onInvalidRemoved) {

        return program.flatMap(sr -> {
            if (signalsValidInterface(sr)) {
                return just(sr);
            }
            else {
                return effects(
                    () -> onInvalidAdded.accept(sr),
                    () -> onInvalidRemoved.accept(sr)).
                    then(nothing());
            }
        });
    }

    private OSGi<ServiceRegistration<Servlet>> registerCXFServletService(
        Bus bus, Map<String, Object> serviceProperties,
        CachingServiceReference<ServletContextHelper> contextReference) {

        String address = canonicalizeAddress(
            getApplicationBase(serviceProperties::get));

        String applicationName = getServiceName(serviceProperties::get);

        Supplier<Map<String, ?>> contextPropertiesSup;

        OSGi<?> program = effects(NOOP, NOOP);

        if (contextReference == null) {
            contextPropertiesSup = () -> {
                HashMap<String, Object> contextProperties = new HashMap<>();

                Utils.mergePropertyMaps(contextProperties, serviceProperties);

                Utils.mergePropertyMaps(contextProperties, _configurationMap);

                contextProperties.putIfAbsent(
                    HTTP_WHITEBOARD_TARGET, "(osgi.http.endpoint=*)");

                String contextName;

                if ("".equals(address)) {
                    contextName = HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME;
                } else {
                    contextName = "context.for" + applicationName;
                }

                contextProperties.put(
                    HTTP_WHITEBOARD_CONTEXT_NAME, contextName);
                contextProperties.put(
                    HTTP_WHITEBOARD_CONTEXT_PATH,
                    address.equals("") ? "/" : address);

                return contextProperties;
            };

            if (!"".equals(address)) {
                program = OSGi.register(
                    ServletContextHelper.class,
                    () -> new ServletContextHelper(
                        _bundleContext.getBundle(
                            (long)serviceProperties.get(
                                "original.service.bundleid"))) {
                        },
                contextPropertiesSup);
            }
        }
        else {
            contextPropertiesSup = () -> {
                HashMap<String, Object> properties = new HashMap<>();

                properties.put(
                    HTTP_WHITEBOARD_CONTEXT_NAME,
                    contextReference.getProperty(HTTP_WHITEBOARD_CONTEXT_NAME));

                return properties;
            };
        }

        Supplier<Map<String, ?>> servletPropertiesSup = () -> {
            HashMap<String, Object> servletProperties = new HashMap<>();

            Utils.mergePropertyMaps(servletProperties, serviceProperties);

            Utils.mergePropertyMaps(servletProperties, _configurationMap);

            Map<String, ?> contextProperties = contextPropertiesSup.get();

            servletProperties.put(
                HTTP_WHITEBOARD_CONTEXT_SELECT,
                format("(%s=%s)", HTTP_WHITEBOARD_CONTEXT_NAME,
                    contextProperties.get(HTTP_WHITEBOARD_CONTEXT_NAME)));
            servletProperties.putIfAbsent(
                HTTP_WHITEBOARD_TARGET, "(osgi.http.endpoint=*)");
            servletProperties.putIfAbsent(
                "servlet.init.hide-service-list-page", "true");

            if (contextReference == null) {
                servletProperties.put(HTTP_WHITEBOARD_SERVLET_PATTERN, "/*");
            }
            else {
                servletProperties.put(
                    HTTP_WHITEBOARD_SERVLET_PATTERN, address + "/*");
            }
            servletProperties.put(
                HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, true);
            servletProperties.put(
                HTTP_WHITEBOARD_SERVLET_NAME, "cxf-servlet");

            return servletProperties;
        };

        return program.then(
            register(
                Servlet.class,
                new PrototypeServiceFactory<Servlet>() {
                    @Override
                    public Servlet getService(
                        Bundle bundle,
                        ServiceRegistration<Servlet> registration) {

                        return createCXFServlet(bus);
                    }

                    @Override
                    public void ungetService(
                        Bundle bundle,
                        ServiceRegistration<Servlet> registration,
                        Servlet service) {

                    }
                },
                servletPropertiesSup));
    }

    private static boolean signalsValidInterface(
        CachingServiceReference<Object> serviceReference) {

        String[] objectClasses = canonicalize(serviceReference.getProperty(
            "objectClass"));

        return Arrays.stream(objectClasses).
            anyMatch(SUPPORTED_EXTENSION_INTERFACES::containsKey);
    }

    private interface ChangeCounter {

        void inc();

    }

    private static class ServiceRegistrationChangeCounter
        implements ChangeCounter{

        private static final String changecount = "service.changecount";
        private final AtomicLong _atomicLong = new AtomicLong();
        private final ServiceRegistration<?> _serviceRegistration;

        ServiceRegistrationChangeCounter(
            ServiceRegistration<?> serviceRegistration) {

            _serviceRegistration = serviceRegistration;
        }

        @Override
        public void inc() {
            long l = _atomicLong.incrementAndGet();

            synchronized (_serviceRegistration) {
                updateProperty(_serviceRegistration, changecount, l);
            }
        }
    }

    private static final Filter _extensionsFilter;

    private static final Filter _resourcesFilter;

    private static Filter _applicationsFilter;

    static {
        try {
            _applicationsFilter = FrameworkUtil.createFilter(
                format(
                    "(&(objectClass=%s)(%s=*))", Application.class.getName(),
                    JAX_RS_APPLICATION_BASE));
            _extensionsFilter = FrameworkUtil.createFilter(
                format("(%s=true)", JAX_RS_EXTENSION));
            _resourcesFilter = FrameworkUtil.createFilter(
                format("(%s=true)", JAX_RS_RESOURCE));
        }
        catch (InvalidSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private class ApplicationReferenceWithContext
        implements Comparable<ApplicationReferenceWithContext> {

        @Override
        public int compareTo(ApplicationReferenceWithContext o) {
            return _applicationReference.compareTo(o._applicationReference);
        }

        private CachingServiceReference<ServletContextHelper> _contextReference;
        private CachingServiceReference<Application> _applicationReference;

        public ApplicationReferenceWithContext(
            CachingServiceReference<ServletContextHelper> contextReference,
            CachingServiceReference<Application> applicationReference) {

            _contextReference = contextReference;
            _applicationReference = applicationReference;
        }

        public String getActualBasePath() {
            String applicationBase = getApplicationBase(
                _applicationReference::getProperty);

            if (_contextReference == null) {
                return applicationBase;
            }

            Object property = _contextReference.getProperty(
                HTTP_WHITEBOARD_CONTEXT_PATH);

            if (property == null) {
                return applicationBase;
            }

            String contextPath = property.toString();

            return contextPath + applicationBase;
        }

        public CachingServiceReference<ServletContextHelper>
            getContextReference() {

            return _contextReference;
        }

        public CachingServiceReference<Application> getApplicationReference() {
            return _applicationReference;
        }

    }

}
