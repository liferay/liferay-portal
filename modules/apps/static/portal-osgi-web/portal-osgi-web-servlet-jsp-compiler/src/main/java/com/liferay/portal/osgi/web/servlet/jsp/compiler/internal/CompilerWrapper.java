/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.jsp.compiler.internal;

import com.liferay.petra.concurrent.ConcurrentReferenceKeyHashMap;
import com.liferay.petra.concurrent.ConcurrentReferenceValueHashMap;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.osgi.web.servlet.jsp.compiler.internal.util.ClassPathUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.lang.reflect.Field;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import javax.servlet.ServletContext;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.apache.jasper.EmbeddedServletOptions;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.Options;
import org.apache.jasper.compiler.Compiler;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.JavacErrorDetail;
import org.apache.jasper.compiler.JspRuntimeContext;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.SmapStratum;
import org.apache.jasper.compiler.SmapUtil;
import org.apache.jasper.compiler.TldCache;
import org.apache.jasper.servlet.JspServletWrapper;
import org.apache.tomcat.util.descriptor.DigesterFactory;
import org.apache.tomcat.util.descriptor.LocalResolver;
import org.apache.tomcat.util.descriptor.XmlIdentifiers;
import org.apache.tomcat.util.descriptor.tld.TaglibXml;
import org.apache.tomcat.util.descriptor.tld.TldParser;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;
import org.apache.tomcat.util.digester.Digester;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Matthew Tambara
 */
public class CompilerWrapper extends Compiler {

	@Override
	public void compile(boolean compileClass) throws Exception {
		String className = ctxt.getFQCN();

		Long lastModifiedTime = _jspClassLastModifiedTimes.get(className);

		if (lastModifiedTime != null) {
			return;
		}

		super.compile(compileClass);
	}

	@Override
	public void init(
		JspCompilationContext jspCompilationContext,
		JspServletWrapper jspServletWrapper) {

		super.init(jspCompilationContext, jspServletWrapper);

		ctxt.checkOutputDir();

		_init(jspCompilationContext);
	}

	@Override
	public boolean isOutDated() {
		String className = ctxt.getFQCN();

		URL url = _getClassURL(className);

		if (url == null) {
			return super.isOutDated();
		}

		try {
			long lastModified = URLUtil.getLastModifiedTime(url);

			Long lastModifiedTime = _jspClassLastModifiedTimes.get(className);

			if ((lastModifiedTime != null) &&
				(lastModified <= lastModifiedTime)) {

				return false;
			}

			_jspClassLastModifiedTimes.put(className, lastModified);

			return true;
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to determine if " + className + " is outdated",
				ioException);
		}

		return super.isOutDated();
	}

	@Override
	public void removeGeneratedFiles() {
		Long lastModifiedTime = _jspClassLastModifiedTimes.get(ctxt.getFQCN());

		if (lastModifiedTime != null) {
			return;
		}

		super.removeGeneratedFiles();
	}

	@Override
	protected void generateClass(Map<String, SmapStratum> smapStratums)
		throws Exception {

		DiagnosticCollector<JavaFileObject> diagnosticCollector = _compile(
			ctxt.getServletClassName(), errDispatcher);

		if (!ctxt.keepGenerated()) {
			File javaFile = new File(ctxt.getServletJavaFileName());

			if (!javaFile.delete()) {
				throw new JasperException(
					Localizer.getMessage(
						"jsp.warning.compiler.javafile.delete.fail", javaFile));
			}
		}

		if (diagnosticCollector != null) {
			List<Diagnostic<? extends JavaFileObject>> diagnostics =
				diagnosticCollector.getDiagnostics();

			JavacErrorDetail[] javacErrorDetails =
				new JavacErrorDetail[diagnostics.size()];

			for (int i = 0; i < diagnostics.size(); i++) {
				Diagnostic<? extends JavaFileObject> diagnostic =
					diagnostics.get(i);

				javacErrorDetails[i] = ErrorDispatcher.createJavacError(
					ctxt.getServletJavaFileName(), pageNodes,
					new StringBuilder(diagnostic.getMessage(null)),
					(int)diagnostic.getLineNumber());
			}

			errDispatcher.javacError(javacErrorDetails);

			return;
		}

		if (ctxt.isPrototypeMode()) {
			return;
		}

		if (!options.isSmapSuppressed()) {
			SmapUtil.installSmap(smapStratums);
		}
	}

	@Override
	protected Map<String, SmapStratum> generateJava() throws Exception {
		Map<String, SmapStratum> smapStratums = super.generateJava();

		if (_textReplacerBiFunction == null) {
			return smapStratums;
		}

		File javaFile = new File(ctxt.getServletJavaFileName());

		String content = StreamUtil.toString(new FileInputStream(javaFile));

		String newContent = _textReplacerBiFunction.apply(
			"ModuleJspCJava#" + javaFile, content);

		if (!newContent.equals(content)) {
			Files.write(
				javaFile.toPath(), newContent.getBytes(StringPool.UTF8),
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
		}

		return smapStratums;
	}

	private static Set<String> _collectPackageNames(BundleWiring bundleWiring) {
		Set<String> packageNames = _bundleWiringPackageNamesCache.get(
			bundleWiring);

		if (packageNames != null) {
			return packageNames;
		}

		packageNames = new HashSet<>();

		for (BundleCapability bundleCapability :
				bundleWiring.getCapabilities(
					BundleRevision.PACKAGE_NAMESPACE)) {

			Map<String, Object> attributes = bundleCapability.getAttributes();

			Object packageName = attributes.get(
				BundleRevision.PACKAGE_NAMESPACE);

			if (packageName != null) {
				packageNames.add((String)packageName);
			}
		}

		_bundleWiringPackageNamesCache.put(bundleWiring, packageNames);

		return packageNames;
	}

	private static void _resolveId(
		Map<String, String> ids, String id, String name, boolean addSelf) {

		if (ids.containsKey(id)) {
			return;
		}

		Class<?> clazz = CompilerWrapper.class;

		URL url = clazz.getResource(
			"/javax/servlet/jsp/resources/".concat(name));

		String location = null;

		if (url != null) {
			location = url.toExternalForm();
		}

		if (location != null) {
			ids.put(id, location);

			if (addSelf) {
				ids.put(location, location);
			}
		}
	}

	private void _addDependenciesToClassPath() {
		ClassLoader frameworkClassLoader = Bundle.class.getClassLoader();

		for (String className : _JSP_COMPILER_DEPENDENCIES) {
			try {
				Class<?> clazz = Class.forName(
					className, true, frameworkClassLoader);

				_addDependencyToClassPath(clazz);
			}
			catch (ClassNotFoundException classNotFoundException) {
				_log.error(
					"Unable to add depedency " + className +
						" to the classpath",
					classNotFoundException);
			}
		}
	}

	private void _addDependencyToClassPath(Class<?> clazz) {
		ProtectionDomain protectionDomain = clazz.getProtectionDomain();

		if (protectionDomain == null) {
			return;
		}

		CodeSource codeSource = protectionDomain.getCodeSource();

		URL url = codeSource.getLocation();

		try {
			File file = ClassPathUtil.getFile(url);

			if ((file == null) && _log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Ignoring URL ", url, " because of unknown protocol ",
						url.getProtocol()));
			}

			if (file.exists() && file.canRead()) {
				_classPath.remove(file);

				_classPath.add(0, file);
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private void _collectTLDMappings(
			Bundle bundle, Map<TldResourcePath, TaglibXml> taglibXmls,
			Map<String, TldResourcePath> tldResourcePaths)
		throws IOException {

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		List<String> resourcePaths = new ArrayList<>(
			bundleWiring.listResources(
				"/META-INF/", "*.tld", BundleWiring.LISTRESOURCES_RECURSE));

		resourcePaths.addAll(
			bundleWiring.listResources(
				"/WEB-INF/", "*.tld", BundleWiring.LISTRESOURCES_RECURSE));

		for (String resourcePath : resourcePaths) {
			URL url = bundle.getResource(resourcePath);

			if (url == null) {
				continue;
			}

			_populateTldMappings(
				StringPool.SLASH.concat(resourcePath), taglibXmls,
				tldResourcePaths, url);
		}

		List<URL> urls = new ArrayList<>(
			bundleWiring.findEntries(
				"/META-INF/", "*.tld", BundleWiring.LISTRESOURCES_RECURSE));

		urls.addAll(
			bundleWiring.findEntries(
				"/WEB-INF/", "*.tld", BundleWiring.LISTRESOURCES_RECURSE));

		for (URL url : urls) {
			_populateTldMappings(
				url.getPath(), taglibXmls, tldResourcePaths, url);
		}
	}

	private DiagnosticCollector<JavaFileObject> _compile(
			String className, ErrorDispatcher errorDispatcher)
		throws Exception {

		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

		if (javaCompiler == null) {
			errorDispatcher.jspError("jsp.error.nojdk");

			throw new JasperException("Unable to find Java compiler");
		}

		DiagnosticCollector<JavaFileObject> diagnosticCollector =
			new DiagnosticCollector<>();

		StandardJavaFileManager standardJavaFileManager =
			javaCompiler.getStandardFileManager(
				diagnosticCollector, null, null);

		try {
			standardJavaFileManager.setLocation(
				StandardLocation.CLASS_PATH, _classPath);
		}
		catch (IOException ioException) {
			throw new JasperException(ioException);
		}

		try (BundleJavaFileManager bundleJavaFileManager =
				new BundleJavaFileManager(
					_classLoader, standardJavaFileManager,
					_javaFileObjectResolvers)) {

			JavaCompiler.CompilationTask compilationTask = javaCompiler.getTask(
				null, bundleJavaFileManager, diagnosticCollector,
				_compilerOptions, null,
				Collections.singletonList(
					new StringJavaFileObject(
						className.substring(
							className.lastIndexOf(CharPool.PERIOD) + 1),
						FileUtil.read(
							_jspCompilationContext.getServletJavaFileName()))));

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Compiling JSP: ".concat(_jspCompilationContext.getFQCN()));
			}

			if (compilationTask.call()) {
				_saveClassFile(
					bundleJavaFileManager.getBytecodeJavaFileObjects(),
					_jspCompilationContext.getFQCN(),
					_jspCompilationContext.getClassFileName());

				return null;
			}
		}
		catch (IOException ioException) {
			throw new JasperException(ioException);
		}

		return diagnosticCollector;
	}

	private URL _getClassURL(String className) {
		Options options = ctxt.getOptions();

		EmbeddedServletOptions embeddedServletOptions =
			(EmbeddedServletOptions)options;

		if (Boolean.valueOf(
				embeddedServletOptions.getProperty("hasFragment"))) {

			return null;
		}

		JspRuntimeContext jspRuntimeContext = ctxt.getRuntimeContext();

		ClassLoader classLoader = jspRuntimeContext.getParentClassLoader();

		try {
			Enumeration<URL> enumeration = classLoader.getResources(
				"/META-INF/resources" + ctxt.getJspFile());

			if (enumeration.hasMoreElements()) {
				enumeration.nextElement();

				if (enumeration.hasMoreElements()) {
					return null;
				}
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		String classNamePath = StringUtil.replace(
			className, CharPool.PERIOD, File.separatorChar);

		classNamePath = classNamePath.concat(".class");

		return classLoader.getResource(classNamePath);
	}

	private void _init(JspCompilationContext jspCompilationContext) {
		_jspCompilationContext = jspCompilationContext;

		_compilerOptions.add("-XDuseUnsharedTable");
		_compilerOptions.add("-proc:none");

		String extDirs = System.getProperty("java.ext.dirs");

		if (extDirs != null) {
			_compilerOptions.add("-extdirs");
			_compilerOptions.add(extDirs);
		}

		Options options = jspCompilationContext.getOptions();

		if (options.getClassDebugInfo()) {
			_compilerOptions.add("-g");
		}
		else {
			_compilerOptions.add("-g:none");
		}

		_compilerOptions.add("-source");
		_compilerOptions.add(options.getCompilerSourceVM());

		_compilerOptions.add("-target");
		_compilerOptions.add(options.getCompilerTargetVM());

		_classPath.add(options.getScratchDir());

		ServletContext servletContext =
			jspCompilationContext.getServletContext();

		ClassLoader classLoader = servletContext.getClassLoader();

		if (!(classLoader instanceof JspBundleClassloader)) {
			throw new IllegalStateException(
				"Class loader is not an instance of JspBundleClassloader");
		}

		JspBundleClassloader jspBundleClassloader =
			(JspBundleClassloader)classLoader;

		_allParticipatingBundles = jspBundleClassloader.getBundles();

		Bundle bundle = _allParticipatingBundles[0];

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		_classLoader = bundleWiring.getClassLoader();

		for (Bundle participatingBundle : _allParticipatingBundles) {
			bundleWiring = participatingBundle.adapt(BundleWiring.class);

			for (BundleWire bundleWire : bundleWiring.getRequiredWires(null)) {
				BundleWiring providedBundleWiring =
					bundleWire.getProviderWiring();

				_bundleWiringPackageNames.put(
					providedBundleWiring,
					_collectPackageNames(providedBundleWiring));
			}

			_javaFileObjectResolvers.add(
				new JavaFileObjectResolver(
					bundleWiring, _bundleWiringPackageNames));
		}

		if (_log.isInfoEnabled()) {
			StringBundler sb = new StringBundler(
				(_bundleWiringPackageNames.size() * 4) + 6);

			sb.append("JSP compiler for bundle ");
			sb.append(bundle.getSymbolicName());
			sb.append(StringPool.DASH);
			sb.append(bundle.getVersion());
			sb.append(" has dependent bundle wirings: ");

			for (BundleWiring curBundleWiring :
					_bundleWiringPackageNames.keySet()) {

				Bundle currentBundle = curBundleWiring.getBundle();

				sb.append(currentBundle.getSymbolicName());

				sb.append(StringPool.DASH);
				sb.append(currentBundle.getVersion());
				sb.append(StringPool.COMMA_AND_SPACE);
			}

			sb.setIndex(sb.index() - 1);

			_log.info(sb.toString());
		}

		jspCompilationContext.setClassLoader(jspBundleClassloader);

		_addDependenciesToClassPath();
		_initTLDMappings(options, servletContext);
	}

	@SuppressWarnings("unchecked")
	private void _initTLDMappings(
		Options options, ServletContext servletContext) {

		Map<TldResourcePath, TaglibXml> taglibXmls = new HashMap<>();
		Map<String, TldResourcePath> tldResourcePaths = new HashMap<>();

		try {
			for (Bundle bundle : _allParticipatingBundles) {
				_collectTLDMappings(bundle, taglibXmls, tldResourcePaths);
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		Map<String, String> map =
			(Map<String, String>)servletContext.getAttribute(
				"jsp.taglib.mappings");

		if (map != null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				try {
					URL url = servletContext.getResource(entry.getValue());

					if (url == null) {
						continue;
					}

					TldResourcePath tldResourcePath = new TldResourcePath(
						url, entry.getValue());

					tldResourcePaths.put(entry.getValue(), tldResourcePath);

					TldParser tldParser = new TldParser(true, false, true);

					taglibXmls.put(
						tldResourcePath, tldParser.parse(tldResourcePath));
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			}
		}

		TldCache tldCache = new TldCache(
			servletContext, tldResourcePaths, taglibXmls);

		servletContext.setAttribute(
			TldCache.SERVLET_CONTEXT_ATTRIBUTE_NAME, tldCache);

		if (options instanceof EmbeddedServletOptions) {
			EmbeddedServletOptions embeddedServletOptions =
				(EmbeddedServletOptions)options;

			embeddedServletOptions.setTldCache(tldCache);
		}
	}

	private void _populateTldMappings(
			String absoluteResourcePath,
			Map<TldResourcePath, TaglibXml> taglibXmls,
			Map<String, TldResourcePath> tldResourcePaths, URL url)
		throws IOException {

		String uri = TldURIUtil.getTldURI(url);

		if ((uri == null) || tldResourcePaths.containsKey(uri)) {
			return;
		}

		uri = uri.trim();

		try {
			TldResourcePath tldResourcePath = new TldResourcePath(
				url, absoluteResourcePath);

			tldResourcePaths.put(uri, tldResourcePath);

			TldParser tldParser = new TldParser(true, false, true);

			Digester digester = (Digester)_digesterField.get(tldParser);

			digester.setEntityResolver(
				new LocalResolver(
					_servletApiPublicIdsMap, _servletApiSystemIdsMap, true));

			taglibXmls.put(tldResourcePath, tldParser.parse(tldResourcePath));
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private void _saveClassFile(
		List<BytecodeJavaFileObject> bytecodeJavaFileObjects, String className,
		String classFileName) {

		for (BytecodeJavaFileObject bytecodeJavaFileObject :
				bytecodeJavaFileObjects) {

			String bytecodeFileClassName =
				bytecodeJavaFileObject.getClassName();
			String outputFileName = classFileName;

			if (!className.equals(bytecodeFileClassName)) {
				outputFileName = outputFileName.substring(
					0, outputFileName.lastIndexOf(File.separator) + 1);

				outputFileName = outputFileName.concat(
					bytecodeFileClassName.substring(
						bytecodeFileClassName.lastIndexOf(CharPool.PERIOD) + 1)
				).concat(
					".class"
				);
			}

			try (FileOutputStream fileOutputStream = new FileOutputStream(
					outputFileName)) {

				StreamUtil.transfer(
					bytecodeJavaFileObject.openInputStream(), fileOutputStream);
			}
			catch (IOException ioException) {
				ServletContext servletContext =
					_jspCompilationContext.getServletContext();

				servletContext.log("Unable to save class file", ioException);
			}
		}
	}

	private static final String[] _JSP_COMPILER_DEPENDENCIES = {
		"com.liferay.portal.kernel.exception.PortalException",
		"com.liferay.portal.util.PortalImpl", "javax.portlet.PortletException",
		"javax.servlet.ServletException"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		CompilerWrapper.class);

	private static final Map<BundleWiring, Set<String>>
		_bundleWiringPackageNamesCache = new ConcurrentReferenceKeyHashMap<>(
			new ConcurrentReferenceValueHashMap<>(
				FinalizeManager.SOFT_REFERENCE_FACTORY),
			FinalizeManager.WEAK_REFERENCE_FACTORY);
	private static final Field _digesterField;
	private static final Map<BundleWiring, Set<String>>
		_jspBundleWiringPackageNames = new HashMap<>();
	private static final Map<String, String> _servletApiPublicIdsMap;
	private static final Map<String, String> _servletApiSystemIdsMap;
	private static final BiFunction<String, String, String>
		_textReplacerBiFunction;

	static {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();

		Object instance = null;

		try {
			Class<?> clazz = classLoader.loadClass(
				"com.liferay.portal.tools.jakarta.ee.transformer.function." +
					"TextReplacerBiFunction");

			instance = clazz.newInstance();
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			if (!(reflectiveOperationException instanceof
					ClassNotFoundException)) {

				throw new ExceptionInInitializerError(
					reflectiveOperationException);
			}
		}

		_textReplacerBiFunction = (BiFunction<String, String, String>)instance;

		Bundle jspBundle = FrameworkUtil.getBundle(CompilerWrapper.class);

		BundleWiring jspBundleWiring = jspBundle.adapt(BundleWiring.class);

		for (BundleWire bundleWire : jspBundleWiring.getRequiredWires(null)) {
			BundleWiring providedBundleWiring = bundleWire.getProviderWiring();

			Set<String> packageNames = _collectPackageNames(
				providedBundleWiring);

			_jspBundleWiringPackageNames.put(
				providedBundleWiring, packageNames);
		}

		try {
			_digesterField = ReflectionUtil.getDeclaredField(
				TldParser.class, "digester");
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}

		Map<String, String> publicIdsMap = HashMapBuilder.putAll(
			DigesterFactory.SERVLET_API_PUBLIC_IDS
		).build();

		_resolveId(
			publicIdsMap, XmlIdentifiers.TLD_11_PUBLIC,
			"web-jsptaglibrary_1_1.dtd", false);
		_resolveId(
			publicIdsMap, XmlIdentifiers.TLD_12_PUBLIC,
			"web-jsptaglibrary_1_2.dtd", false);

		_servletApiPublicIdsMap = Collections.unmodifiableMap(publicIdsMap);

		Map<String, String> systemIdsMap = HashMapBuilder.putAll(
			DigesterFactory.SERVLET_API_SYSTEM_IDS
		).build();

		_resolveId(
			systemIdsMap, XmlIdentifiers.TLD_20_XSD,
			"web-jsptaglibrary_2_0.xsd", false);
		_resolveId(
			systemIdsMap, XmlIdentifiers.TLD_21_XSD,
			"web-jsptaglibrary_2_1.xsd", false);
		_resolveId(systemIdsMap, "jsp_2_0.xsd", "jsp_2_0.xsd", true);
		_resolveId(systemIdsMap, "jsp_2_1.xsd", "jsp_2_1.xsd", true);
		_resolveId(systemIdsMap, "jsp_2_2.xsd", "jsp_2_2.xsd", true);
		_resolveId(systemIdsMap, "jsp_2_3.xsd", "jsp_2_3.xsd", true);

		_servletApiSystemIdsMap = Collections.unmodifiableMap(systemIdsMap);
	}

	private Bundle[] _allParticipatingBundles;
	private final Map<BundleWiring, Set<String>> _bundleWiringPackageNames =
		new HashMap<>(_jspBundleWiringPackageNames);
	private ClassLoader _classLoader;
	private final List<File> _classPath = new ArrayList<>();
	private final List<String> _compilerOptions = new ArrayList<>();
	private final List<JavaFileObjectResolver> _javaFileObjectResolvers =
		new ArrayList<>();
	private final Map<String, Long> _jspClassLastModifiedTimes =
		new ConcurrentHashMap<>();
	private JspCompilationContext _jspCompilationContext;

}