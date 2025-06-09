/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.db.IndexMetadataFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.cache.CacheField;
import com.liferay.portal.kernel.plugin.Version;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.StringUtil_IW;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.Validator_IW;
import com.liferay.portal.tools.ArgumentsUtil;
import com.liferay.portal.tools.GitUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.portal.tools.java.parser.JavaParser;
import com.liferay.portal.xml.SAXReaderFactory;
import com.liferay.util.xml.XMLSafeReader;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.library.ClassLibraryBuilder;
import com.thoughtworks.qdox.library.SortedClassLibraryBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.impl.AbstractBaseJavaEntity;
import com.thoughtworks.qdox.model.impl.DefaultJavaMethod;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;
import com.thoughtworks.qdox.model.impl.DefaultJavaTypeVariable;

import freemarker.cache.ClassTemplateLoader;

import freemarker.ext.beans.BeansWrapper;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateHashModel;

import java.beans.Introspector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * @author Brian Wing Shun Chan
 * @author Charles May
 * @author Alexander Chow
 * @author Harry Mark
 * @author Tariq Dweik
 * @author Glenn Powell
 * @author Raymond Augé
 * @author Prashant Dighe
 * @author Shuyang Zhou
 * @author James Lefeu
 * @author Miguel Pastor
 * @author Cody Hoag
 * @author James Hinkey
 * @author Hugo Huijser
 */
public class ServiceBuilder {

	public static final String AUTHOR = "Brian Wing Shun Chan";

	public static boolean hasAnnotation(
		AbstractBaseJavaEntity abstractBaseJavaEntity, String annotationName) {

		List<JavaAnnotation> javaAnnotations =
			abstractBaseJavaEntity.getAnnotations();

		if (javaAnnotations == null) {
			return false;
		}

		for (JavaAnnotation javaAnnotation : javaAnnotations) {
			JavaClass javaClass = javaAnnotation.getType();

			if (annotationName.equals(javaClass.getName())) {
				return true;
			}
		}

		return false;
	}

	public static void main(String[] args) throws Exception {
		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		String apiDirName = arguments.get("service.api.dir");
		boolean autoImportDefaultReferences = GetterUtil.getBoolean(
			arguments.get("service.auto.import.default.references"), true);
		boolean autoNamespaceTables = GetterUtil.getBoolean(
			arguments.get("service.auto.namespace.tables"));
		String beanLocatorUtil = arguments.get("service.bean.locator.util");
		long buildNumber = GetterUtil.getLong(
			arguments.get("service.build.number"), 1);
		boolean buildNumberIncrement = GetterUtil.getBoolean(
			arguments.get("service.build.number.increment"), true);
		int databaseNameMaxLength = GetterUtil.getInteger(
			arguments.get("service.database.name.max.length"), 30);
		String hbmFileName = arguments.get("service.hbm.file");
		String implDirName = arguments.get("service.impl.dir");
		String[] incubationFeatures = StringUtil.split(
			arguments.get("service.incubation.features"));
		String inputFileName = arguments.get("service.input.file");
		String[] modelHintsConfigs = StringUtil.split(
			GetterUtil.getString(
				arguments.get("service.model.hints.configs"),
				StringUtil.merge(ServiceBuilderArgs.MODEL_HINTS_CONFIGS)));
		String modelHintsFileName = arguments.get("service.model.hints.file");
		boolean osgiModule = GetterUtil.getBoolean(
			arguments.get("service.osgi.module"));
		String pluginName = arguments.get("service.plugin.name");
		String propsUtil = arguments.get("service.props.util");
		String[] readOnlyPrefixes = StringUtil.split(
			GetterUtil.getString(
				arguments.get("service.read.only.prefixes"),
				StringUtil.merge(ServiceBuilderArgs.READ_ONLY_PREFIXES)));
		String[] resourceActionsConfigs = StringUtil.split(
			GetterUtil.getString(
				arguments.get("service.resource.actions.configs"),
				StringUtil.merge(ServiceBuilderArgs.RESOURCE_ACTION_CONFIGS)));
		String resourcesDirName = arguments.get("service.resources.dir");
		String springFileName = arguments.get("service.spring.file");
		String[] springNamespaces = StringUtil.split(
			arguments.get("service.spring.namespaces"));
		String sqlDirName = arguments.get("service.sql.dir");
		String sqlFileName = arguments.get("service.sql.file");
		String sqlIndexesFileName = arguments.get("service.sql.indexes.file");
		String sqlSequencesFileName = arguments.get(
			"service.sql.sequences.file");
		String targetEntityName = arguments.get("service.target.entity.name");
		String testDirName = arguments.get("service.test.dir");
		String uadDirName = arguments.get("service.uad.dir");

		Set<String> resourceActionModels = readResourceActionModels(
			implDirName, resourcesDirName, resourceActionsConfigs);

		ModelHintsUtil modelHintsUtil = new ModelHintsUtil();

		ModelHintsImpl modelHintsImpl = new ModelHintsImpl();

		modelHintsImpl.setModelHintsConfigs(modelHintsConfigs);

		modelHintsImpl.afterPropertiesSet();

		modelHintsUtil.setModelHints(modelHintsImpl);

		try {
			ServiceBuilder serviceBuilder = new ServiceBuilder(
				apiDirName, autoImportDefaultReferences, autoNamespaceTables,
				beanLocatorUtil, buildNumber, buildNumberIncrement,
				databaseNameMaxLength, hbmFileName, implDirName,
				incubationFeatures, inputFileName, modelHintsFileName,
				osgiModule, pluginName, propsUtil, readOnlyPrefixes,
				resourceActionModels, resourcesDirName, springFileName,
				springNamespaces, sqlDirName, sqlFileName, sqlIndexesFileName,
				sqlSequencesFileName, targetEntityName, testDirName, uadDirName,
				true);

			String modifiedFileNames = StringUtil.merge(
				serviceBuilder.getModifiedFileNames());

			System.setProperty(
				ServiceBuilderArgs.OUTPUT_KEY_MODIFIED_FILES,
				modifiedFileNames);
		}
		catch (Exception exception) {
			if (exception instanceof ServiceBuilderException) {
				System.err.println(exception.getMessage());
			}
			else {
				String message = StringBundler.concat(
					"Please set these arguments. Sample values are:\n\n",
					"\tservice.api.dir=${basedir}/../portal-kernel/src\n",
					"\tservice.auto.import.default.references=true\n",
					"\tservice.auto.namespace.tables=false\n",
					"\tservice.bean.locator.util=com.liferay.portal.kernel.",
					"bean.PortalBeanLocatorUtil\n",
					"\tservice.build.number=1\n",
					"\tservice.build.number.increment=true\n",
					"\tservice.hbm.file=",
					"${basedir}/src/META-INF/portal-hbm.xml\n",
					"\tservice.impl.dir=${basedir}/src\n",
					"\tservice.input.file=${service.file}\n",
					"\tservice.model.hints.configs=",
					StringUtil.merge(ServiceBuilderArgs.MODEL_HINTS_CONFIGS),
					"\n", "\tservice.model.hints.file=",
					"${basedir}/src/META-INF/portal-model-hints.xml\n",
					"\tservice.osgi.module=false\n", "\tservice.plugin.name=\n",
					"\tservice.props.util=com.liferay.portal.util.PropsUtil\n",
					"\tservice.read.only.prefixes=",
					StringUtil.merge(ServiceBuilderArgs.READ_ONLY_PREFIXES),
					"\n", "\tservice.resource.actions.configs=",
					StringUtil.merge(
						ServiceBuilderArgs.RESOURCE_ACTION_CONFIGS),
					"\n", "\tservice.resources.dir=${basedir}/src\n",
					"\tservice.spring.file=",
					"${basedir}/src/META-INF/portal-spring.xml\n",
					"\tservice.spring.namespaces=beans\n",
					"\tservice.sql.dir=${basedir}/../sql\n",
					"\tservice.sql.file=portal-tables.sql\n",
					"\tservice.sql.indexes.file=indexes.sql\n",
					"\tservice.sql.sequences.file=sequences.sql\n",
					"\tservice.target.entity.name=",
					"${service.target.entity.name}\n",
					"\tservice.target.kernel.version=",
					"${service.target.kernel.version}\n",
					"\tservice.test.dir=${basedir}/test/integration\n\n",
					"You can also customize the generated code by overriding ",
					"the default templates with these optional system ",
					"properties:\n\n", "\t-Dservice.tpl.bad_alias_names=",
					_TPL_ROOT, "bad_alias_names.txt\n",
					"\t-Dservice.tpl.bad_column_names=", _TPL_ROOT,
					"bad_column_names.txt\n", "\t-Dservice.tpl.bad_json_types=",
					_TPL_ROOT, "bad_json_types.txt\n",
					"\t-Dservice.tpl.bad_table_names=", _TPL_ROOT,
					"bad_table_names.txt\n", "\t-Dservice.tpl.base_mode_impl=",
					_TPL_ROOT, "base_mode_impl.ftl\n",
					"\t-Dservice.tpl.blob_model=", _TPL_ROOT,
					"blob_model.ftl\n",
					"\t-Dservice.tpl.copyright.txt=copyright.txt\n",
					"\t-Dservice.tpl.ejb_pk=", _TPL_ROOT, "ejb_pk.ftl\n",
					"\t-Dservice.tpl.exception=", _TPL_ROOT, "exception.ftl\n",
					"\t-Dservice.tpl.extended_model=", _TPL_ROOT,
					"extended_model.ftl\n",
					"\t-Dservice.tpl.extended_model_base_impl=", _TPL_ROOT,
					"extended_model_base_impl.ftl\n",
					"\t-Dservice.tpl.extended_model_impl=", _TPL_ROOT,
					"extended_model_impl.ftl\n", "\t-Dservice.tpl.finder=",
					_TPL_ROOT, "finder.ftl\n",
					"\t-Dservice.tpl.finder_base_impl=", _TPL_ROOT,
					"finder_base_impl.ftl\n", "\t-Dservice.tpl.finder_util=",
					_TPL_ROOT, "finder_util.ftl\n", "\t-Dservice.tpl.hbm_xml=",
					_TPL_ROOT, "hbm_xml.ftl\n", "\t-Dservice.tpl.json_js=",
					_TPL_ROOT, "json_js.ftl\n",
					"\t-Dservice.tpl.json_js_method=", _TPL_ROOT,
					"json_js_method.ftl\n", "\t-Dservice.tpl.model=", _TPL_ROOT,
					"model.ftl\n", "\t-Dservice.tpl.model_cache=", _TPL_ROOT,
					"model_cache.ftl\n", "\t-Dservice.tpl.model_hints_xml=",
					_TPL_ROOT, "model_hints_xml.ftl\n",
					"\t-Dservice.tpl.model_impl=", _TPL_ROOT,
					"model_impl.ftl\n", "\t-Dservice.tpl.model_soap=",
					_TPL_ROOT, "model_soap.ftl\n",
					"\t-Dservice.tpl.model_wrapper=", _TPL_ROOT,
					"model_wrapper.ftl\n", "\t-Dservice.tpl.persistence=",
					_TPL_ROOT, "persistence.ftl\n",
					"\t-Dservice.tpl.persistence_impl=", _TPL_ROOT,
					"persistence_impl.ftl\n",
					"\t-Dservice.tpl.persistence_util=", _TPL_ROOT,
					"persistence_util.ftl\n", "\t-Dservice.tpl.props=",
					_TPL_ROOT, "props.ftl\n", "\t-Dservice.tpl.service=",
					_TPL_ROOT, "service.ftl\n",
					"\t-Dservice.tpl.service_base_impl=", _TPL_ROOT,
					"service_base_impl.ftl\n", "\t-Dservice.tpl.service_clp=",
					_TPL_ROOT, "service_clp.ftl\n",
					"\t-Dservice.tpl.service_clp_invoker=", _TPL_ROOT,
					"service_clp_invoker.ftl\n",
					"\t-Dservice.tpl.service_clp_message_listener=", _TPL_ROOT,
					"service_clp_message_listener.ftl\n",
					"\t-Dservice.tpl.service_clp_serializer=", _TPL_ROOT,
					"service_clp_serializer.ftl\n",
					"\t-Dservice.tpl.service_http=", _TPL_ROOT,
					"service_http.ftl\n", "\t-Dservice.tpl.service_impl=",
					_TPL_ROOT, "service_impl.ftl\n",
					"\t-Dservice.tpl.service_props_util=", _TPL_ROOT,
					"service_props_util.ftl\n", "\t-Dservice.tpl.service_soap=",
					_TPL_ROOT, "service_soap.ftl\n",
					"\t-Dservice.tpl.service_util=", _TPL_ROOT,
					"service_util.ftl\n", "\t-Dservice.tpl.service_wrapper=",
					_TPL_ROOT, "service_wrapper.ftl\n",
					"\t-Dservice.tpl.spring_xml=", _TPL_ROOT,
					"spring_xml.ftl\n", "\t-Dservice.tpl.spring_xml_session=",
					_TPL_ROOT, "spring_xml_session.ftl");

				System.out.println(message);
			}

			ArgumentsUtil.processMainException(arguments, exception);
		}

		Introspector.flushCaches();
	}

	public static Set<String> readResourceActionModels(
			String implDirName, String resourcesDirName,
			String[] resourceActionsConfigs)
		throws Exception {

		Set<String> resourceActionModels = new HashSet<>();

		ClassLoader classLoader = ServiceBuilder.class.getClassLoader();

		for (String config : resourceActionsConfigs) {
			if (config.startsWith("classpath*:")) {
				String name = config.substring("classpath*:".length());

				Enumeration<URL> enumeration = classLoader.getResources(name);

				while (enumeration.hasMoreElements()) {
					URL url = enumeration.nextElement();

					try (InputStream inputStream = url.openStream()) {
						_readResourceActionModels(
							implDirName, resourcesDirName, inputStream,
							resourceActionModels);
					}
				}
			}
			else {
				Enumeration<URL> enumeration = classLoader.getResources(config);

				if (enumeration.hasMoreElements()) {
					while (enumeration.hasMoreElements()) {
						URL url = enumeration.nextElement();

						try (InputStream inputStream = url.openStream()) {
							_readResourceActionModels(
								implDirName, resourcesDirName, inputStream,
								resourceActionModels);
						}
					}
				}
				else {
					File file = new File(config);

					if (!file.exists()) {
						file = new File(implDirName, config);
					}

					if (!file.exists() &&
						Validator.isNotNull(resourcesDirName)) {

						file = new File(resourcesDirName, config);
					}

					if (!file.exists()) {
						continue;
					}

					try (InputStream inputStream = new FileInputStream(file)) {
						_readResourceActionModels(
							implDirName, resourcesDirName, inputStream,
							resourceActionModels);
					}
				}
			}
		}

		return resourceActionModels;
	}

	public static String toHumanName(String name) {
		if (name == null) {
			return null;
		}

		String humanName = TextFormatter.format(name, TextFormatter.H);

		if (humanName.equals("id")) {
			humanName = "ID";
		}
		else if (humanName.equals("ids")) {
			humanName = "IDs";
		}

		if (humanName.endsWith(" id")) {
			humanName = humanName.substring(0, humanName.length() - 3) + " ID";
		}
		else if (humanName.endsWith(" ids")) {
			humanName = humanName.substring(0, humanName.length() - 4) + " IDs";
		}

		if (humanName.contains(" id ")) {
			humanName = StringUtil.replace(humanName, " id ", " ID ");
		}
		else if (humanName.contains(" ids ")) {
			humanName = StringUtil.replace(humanName, " ids ", " IDs ");
		}

		return humanName;
	}

	public ServiceBuilder(
			String apiDirName, boolean autoImportDefaultReferences,
			boolean autoNamespaceTables, String beanLocatorUtil,
			int databaseNameMaxLength, String hbmFileName, String implDirName,
			String inputFileName, String modelHintsFileName, boolean osgiModule,
			String pluginName, String propsUtil, String[] readOnlyPrefixes,
			Set<String> resourceActionModels, String resourcesDirName,
			String springFileName, String[] springNamespaces, String sqlDirName,
			String sqlFileName, String sqlIndexesFileName,
			String sqlSequencesFileName, String targetEntityName,
			String testDirName, String uadDirName)
		throws Exception {

		this(
			apiDirName, autoImportDefaultReferences, autoNamespaceTables,
			beanLocatorUtil, 1, true, databaseNameMaxLength, hbmFileName,
			implDirName, null, inputFileName, modelHintsFileName, osgiModule,
			pluginName, propsUtil, readOnlyPrefixes, resourceActionModels,
			resourcesDirName, springFileName, springNamespaces, sqlDirName,
			sqlFileName, sqlIndexesFileName, sqlSequencesFileName,
			targetEntityName, testDirName, uadDirName, true);
	}

	public ServiceBuilder(
			String apiDirName, boolean autoImportDefaultReferences,
			boolean autoNamespaceTables, String beanLocatorUtil,
			long buildNumber, boolean buildNumberIncrement,
			int databaseNameMaxLength, String hbmFileName, String implDirName,
			String[] incubationFeatures, String inputFileName,
			String modelHintsFileName, boolean osgiModule, String pluginName,
			String propsUtil, String[] readOnlyPrefixes,
			Set<String> resourceActionModels, String resourcesDirName,
			String springFileName, String[] springNamespaces, String sqlDirName,
			String sqlFileName, String sqlIndexesFileName,
			String sqlSequencesFileName, String targetEntityName,
			String testDirName, String uadDirName, boolean build)
		throws Exception {

		_tplBadAliasNames = _getTplProperty(
			"bad_alias_names", _tplBadAliasNames);
		_tplBadColumnNames = _getTplProperty(
			"bad_column_names", _tplBadColumnNames);
		_tplBadTableNames = _getTplProperty(
			"bad_table_names", _tplBadTableNames);
		_tplBlobModel = _getTplProperty("blob_model", _tplBlobModel);
		_tplEjbPK = _getTplProperty("ejb_pk", _tplEjbPK);
		_tplException = _getTplProperty("exception", _tplException);
		_tplExtendedModel = _getTplProperty(
			"extended_model", _tplExtendedModel);
		_tplExtendedModelBaseImpl = _getTplProperty(
			"extended_model_base_impl", _tplExtendedModelBaseImpl);
		_tplExtendedModelImpl = _getTplProperty(
			"extended_model_impl", _tplExtendedModelImpl);
		_tplFinder = _getTplProperty("finder", _tplFinder);
		_tplFinderBaseImpl = _getTplProperty(
			"finder_base_impl", _tplFinderBaseImpl);
		_tplFinderUtil = _getTplProperty("finder_util", _tplFinderUtil);
		_tplHbmXml = _getTplProperty("hbm_xml", _tplHbmXml);
		_tplJsonJs = _getTplProperty("json_js", _tplJsonJs);
		_tplJsonJsMethod = _getTplProperty("json_js_method", _tplJsonJsMethod);
		_tplModel = _getTplProperty("model", _tplModel);
		_tplModelArgumentsResolver = _getTplProperty(
			"model_arguments_resolver", _tplModelArgumentsResolver);
		_tplModelCache = _getTplProperty("model_cache", _tplModelCache);
		_tplModelHintsXml = _getTplProperty(
			"model_hints_xml", _tplModelHintsXml);
		_tplModelImpl = _getTplProperty("model_impl", _tplModelImpl);
		_tplModelSoap = _getTplProperty("model_soap", _tplModelSoap);
		_tplModelWrapper = _getTplProperty("model_wrapper", _tplModelWrapper);
		_tplPersistence = _getTplProperty("persistence", _tplPersistence);
		_tplPersistenceImpl = _getTplProperty(
			"persistence_impl", _tplPersistenceImpl);
		_tplPersistenceUtil = _getTplProperty(
			"persistence_util", _tplPersistenceUtil);
		_tplProps = _getTplProperty("props", _tplProps);
		_tplService = _getTplProperty("service", _tplService);
		_tplServiceBaseImpl = _getTplProperty(
			"service_base_impl", _tplServiceBaseImpl);
		_tplServiceHttp = _getTplProperty("service_http", _tplServiceHttp);
		_tplServiceImpl = _getTplProperty("service_impl", _tplServiceImpl);
		_tplServicePropsUtil = _getTplProperty(
			"service_props_util", _tplServicePropsUtil);
		_tplServiceSoap = _getTplProperty("service_soap", _tplServiceSoap);
		_tplServiceUtil = _getTplProperty("service_util", _tplServiceUtil);
		_tplServiceWrapper = _getTplProperty(
			"service_wrapper", _tplServiceWrapper);
		_tplSpringXml = _getTplProperty("spring_xml", _tplSpringXml);

		try {
			_apiDirName = _normalize(apiDirName);
			_autoImportDefaultReferences = autoImportDefaultReferences;
			_autoNamespaceTables = autoNamespaceTables;
			_beanLocatorUtil = beanLocatorUtil;
			_buildNumber = buildNumber;
			_buildNumberIncrement = buildNumberIncrement;
			_hbmFileName = _normalize(hbmFileName);
			_implDirName = _normalize(implDirName);
			_incubationFeatures = incubationFeatures;
			_modelHintsFileName = _normalize(modelHintsFileName);
			_osgiModule = osgiModule;
			_pluginName = GetterUtil.getString(pluginName);
			_propsUtil = propsUtil;
			_readOnlyPrefixes = readOnlyPrefixes;
			_resourceActionModels = resourceActionModels;
			_resourcesDirName = _normalize(resourcesDirName);
			_springFileName = _normalize(springFileName);

			_springNamespaces = springNamespaces;

			if (!ArrayUtil.contains(
					_springNamespaces, _SPRING_NAMESPACE_BEANS)) {

				_springNamespaces = ArrayUtil.append(
					_springNamespaces, _SPRING_NAMESPACE_BEANS);
			}

			_sqlDirName = _normalize(sqlDirName);
			_sqlFileName = sqlFileName;
			_sqlIndexesFileName = sqlIndexesFileName;
			_sqlSequencesFileName = sqlSequencesFileName;
			_targetEntityName = targetEntityName;
			_testDirName = _normalize(testDirName);
			_uadDirName = _normalize(uadDirName);
			_build = build;

			_badAliasNames = _readLines(_tplBadAliasNames);
			_badColumnNames = _readLines(_tplBadColumnNames);
			_badTableNames = _readLines(_tplBadTableNames);

			SAXReader saxReader = _getSAXReader();

			Document document = saxReader.read(
				new XMLSafeReader(
					ToolsUtil.getContent(_normalize(inputFileName))));

			DocumentType documentType = document.getDocType();

			Matcher matcher = _dtdVersionPattern.matcher(
				documentType.getSystemID());

			if (matcher.matches()) {
				_dtdVersion = Version.getInstance(
					StringUtil.replace(matcher.group(1), '_', '.'));
			}
			else {
				throw new IllegalArgumentException(
					"Unable to parse DTD version for " + inputFileName);
			}

			_compatProperties = _getCompatProperties(matcher.group(1));

			Collections.addAll(
				_badAliasNames,
				StringUtil.split(
					_compatProperties.getProperty("bad.alias.names.extra")));
			Collections.addAll(
				_badColumnNames,
				StringUtil.split(
					_compatProperties.getProperty("bad.column.names.extra")));
			Collections.addAll(
				_badTableNames,
				StringUtil.split(
					_compatProperties.getProperty("bad.table.names.extra")));

			Element rootElement = document.getRootElement();

			String packagePath = rootElement.attributeValue("package-path");

			if (Validator.isNull(packagePath)) {
				throw new IllegalArgumentException(
					"The package-path attribute is required");
			}

			_databaseNameMaxLength = GetterUtil.getInteger(
				rootElement.attributeValue("database-name-max-length"),
				databaseNameMaxLength);

			_apiPackagePath = GetterUtil.getString(
				rootElement.attributeValue("api-package-path"), packagePath);
			_oldServiceOutputPath =
				_apiDirName + "/" + StringUtil.replace(packagePath, '.', '/');
			_outputPath =
				_implDirName + "/" + StringUtil.replace(packagePath, '.', '/');

			if (Validator.isNotNull(_testDirName)) {
				_testOutputPath =
					_testDirName + "/" +
						StringUtil.replace(packagePath, '.', '/');
			}

			_serviceOutputPath =
				_apiDirName + "/" +
					StringUtil.replace(_apiPackagePath, '.', '/');

			_packagePath = packagePath;

			if (Validator.isNull(_uadDirName)) {
				_uadDirName = StringUtil.replace(_apiDirName, "-api/", "-uad/");
			}

			_autoImportDefaultReferences = GetterUtil.getBoolean(
				rootElement.attributeValue("auto-import-default-references"),
				_autoImportDefaultReferences);
			_autoNamespaceTables = GetterUtil.getBoolean(
				rootElement.attributeValue("auto-namespace-tables"),
				_autoNamespaceTables);
			_changeTrackingEnabled = GetterUtil.getBoolean(
				rootElement.attributeValue("change-tracking-enabled"));

			String dependencyInjector = rootElement.attributeValue(
				"dependency-injector");

			File springFile = new File(_springFileName);

			if ((dependencyInjector != null) || !_osgiModule ||
				isVersionLTE_7_1_0() || springFile.exists()) {

				_dependencyInjectorDS = StringUtil.equalsIgnoreCase(
					dependencyInjector, "ds");
			}
			else {
				_dependencyInjectorDS = true;
			}

			if (_dependencyInjectorDS) {
				if (isVersionLTE_7_1_0()) {
					throw new IllegalArgumentException(
						"Cannot use dependency-injector=\"ds\" without using " +
							"at least DTD version 7.2.0");
				}

				if (!_osgiModule) {
					throw new IllegalArgumentException(
						"Cannot use dependency-injector=\"ds\" with a WAR");
				}
			}

			_mvccEnabled = GetterUtil.getBoolean(
				rootElement.attributeValue("mvcc-enabled"));
			_optimizeDBIndexes = GetterUtil.getBoolean(
				rootElement.attributeValue("optimize-db-indexes"),
				isVersionGTE_7_4_0());
			_shortNoSuchExceptionEnabled = GetterUtil.getBoolean(
				rootElement.attributeValue("short-no-such-exception-enabled"),
				true);

			Element authorElement = rootElement.element("author");

			if (authorElement != null) {
				_author = authorElement.getText();
			}
			else {
				_author = AUTHOR;
			}

			Element portletElement = rootElement.element("portlet");

			if (portletElement != null) {
				_portletShortName = portletElement.attributeValue("short-name");

				String portletPackageName = TextFormatter.format(
					portletElement.attributeValue("name"), TextFormatter.B);

				_apiPackagePath += "." + portletPackageName;
				_outputPath += "/" + portletPackageName;
				_packagePath += "." + portletPackageName;
				_serviceOutputPath += "/" + portletPackageName;
				_testOutputPath += "/" + portletPackageName;
			}
			else {
				Element namespaceElement = rootElement.element("namespace");

				_portletShortName = namespaceElement.getText();
			}

			_portletShortName = _portletShortName.trim();

			char[] portletShortNameChars = _portletShortName.toCharArray();

			for (int i = 0; i < portletShortNameChars.length; i++) {
				char portletShortNameChar = portletShortNameChars[i];

				if (i == 0) {
					if (!Validator.isChar(portletShortNameChar) &&
						(portletShortNameChar != CharPool.UNDERLINE)) {

						throw new RuntimeException(
							"The namespace element must be a valid keyword");
					}
				}
				else {
					if (!Validator.isChar(portletShortNameChar) &&
						!Validator.isDigit(portletShortNameChar) &&
						(portletShortNameChar != CharPool.UNDERLINE)) {

						throw new RuntimeException(
							"The namespace element must be a valid keyword");
					}
				}
			}

			_entities = new ArrayList<>();
			_entityMappings = new HashMap<>();

			List<Element> entityElements = rootElement.elements("entity");

			for (Element entityElement : entityElements) {
				_parseEntity(entityElement);
			}

			List<String> exceptionList = new ArrayList<>();

			Element exceptionsElement = rootElement.element("exceptions");

			if (exceptionsElement != null) {
				List<Element> exceptionElements = exceptionsElement.elements(
					"exception");

				for (Element exceptionElement : exceptionElements) {
					exceptionList.add(exceptionElement.getText());
				}
			}

			if (build) {
				Collections.sort(_entities);

				for (Entity entity : _entities) {
					if (_isTargetEntity(entity)) {
						System.out.println("Building " + entity.getName());

						_resolveEntity(entity);

						_removeOldServices(entity);

						_removeActionableDynamicQuery(entity);
						_removeExportActionableDynamicQuery(entity);

						if (entity.hasEntityColumns()) {
							_createHbm(entity);
							_createHbmUtil(entity);

							_createPersistenceImpl(entity);
							_createPersistence(entity);
							_createPersistenceUtil(entity);

							_createModelArgumentsResolver(entity);

							if (Validator.isNotNull(_testDirName)) {
								_createPersistenceTest(entity);
							}

							_createModelImpl(entity);
							_createExtendedModelBaseImpl(entity);
							_createExtendedModelImpl(entity);

							entity.setTransients(_getTransients(entity, false));
							entity.setParentTransients(
								_getTransients(entity, true));

							_createModel(entity);
							_createExtendedModel(entity);

							_createModelCache(entity);
							_createModelWrapper(entity);

							if (isVersionGTE_7_4_0()) {
								_removeModelSoap(entity, _serviceOutputPath);
							}
							else {
								_createModelSoap(entity);
							}

							_createModelTable(entity);

							_createBlobModels(entity);

							_createPool(entity);

							_createEJBPK(entity);
						}

						_createFinder(entity);
						_createFinderBaseImpl(entity);
						_createFinderUtil(entity);

						if (entity.hasLocalService()) {
							_createServiceImpl(entity, _SESSION_TYPE_LOCAL);
							_createServiceBaseImpl(entity, _SESSION_TYPE_LOCAL);
							_createService(entity, _SESSION_TYPE_LOCAL);
							_createServiceFactory(entity, _SESSION_TYPE_LOCAL);
							_createServiceUtil(entity, _SESSION_TYPE_LOCAL);
							_createServiceWrapper(entity, _SESSION_TYPE_LOCAL);
						}
						else {
							_removeServiceImpl(entity, _SESSION_TYPE_LOCAL);
							_removeServiceBaseImpl(entity, _SESSION_TYPE_LOCAL);
							_removeService(
								entity, _SESSION_TYPE_LOCAL,
								_serviceOutputPath);
							_removeServiceUtil(
								entity, _SESSION_TYPE_LOCAL,
								_serviceOutputPath);
							_removeServiceWrapper(
								entity, _SESSION_TYPE_LOCAL,
								_serviceOutputPath);
						}

						if (entity.hasRemoteService()) {
							_createServiceImpl(entity, _SESSION_TYPE_REMOTE);
							_createServiceBaseImpl(
								entity, _SESSION_TYPE_REMOTE);
							_createService(entity, _SESSION_TYPE_REMOTE);
							_createServiceFactory(entity, _SESSION_TYPE_REMOTE);
							_createServiceUtil(entity, _SESSION_TYPE_REMOTE);
							_createServiceWrapper(entity, _SESSION_TYPE_REMOTE);

							_createServiceHttp(entity);

							_removeServiceJson(entity);

							if (entity.hasEntityColumns()) {
								_removeServiceJsonSerializer(entity);
							}

							if (isVersionGTE_7_4_0()) {
								_removeServiceSoap(entity);
							}
							else {
								_createServiceSoap(entity);
							}
						}
						else {
							_removeServiceImpl(entity, _SESSION_TYPE_REMOTE);
							_removeServiceBaseImpl(
								entity, _SESSION_TYPE_REMOTE);
							_removeService(
								entity, _SESSION_TYPE_REMOTE,
								_serviceOutputPath);
							_removeServiceUtil(
								entity, _SESSION_TYPE_REMOTE,
								_serviceOutputPath);
							_removeServiceWrapper(
								entity, _SESSION_TYPE_REMOTE,
								_serviceOutputPath);

							_removeServiceHttp(entity);

							_removeServiceSoap(entity);
						}

						_createCTServiceImpl(entity);

						if (entity.isUADEnabled()) {
							_createBaseUADAnonymizer(entity);
							_createBaseUADExporter(entity);
							_createUADAnonymizer(entity);
							_createUADExporter(entity);

							if (ListUtil.isEmpty(
									entity.
										getUADNonanonymizableEntityColumns())) {

								_removeBaseUADDisplay(entity);
								_removeUADDisplay(entity);
								_removeUADDisplayTest(entity);
							}
							else {
								_createBaseUADDisplay(entity);
								_createUADDisplay(entity);
							}
						}
						else {
							//_removeBaseUADAnonymizer(entity);
							//_removeBaseUADDisplay(entity);
							//_removeBaseUADExporter(entity);
							//_removeUADAnonymizer(entity);
							//_removeUADAnonymizerTest(entity);
							//_removeUADDisplay(entity);
							//_removeUADDisplayTest(entity);
							//_removeUADExporter(entity);
							//_removeUADExporterTest(entity);
							//_removeUADTestHelper(entity);
						}
					}
					else {
						if (entity.hasEntityColumns()) {
							entity.setTransients(_getTransients(entity, false));
							entity.setParentTransients(
								_getTransients(entity, true));
						}
					}
				}

				for (EntityMapping entityMapping : _entityMappings.values()) {
					_createEntityMappingTable(entityMapping);
				}

				_createHbmXml();
				_createModelHintsXml();
				_createSpringXml();

				_createExceptions(exceptionList);

				_createPersistenceConstants();

				_createServicePropsUtil();
				_createServletContextUtil();

				_createSQLIndexes();
				_createSQLTables();
				_createSQLSequences();

				_createProps();

				for (String uadApplicationName :
						_uadApplicationEntities.keySet()) {

					_createUADBnd(uadApplicationName);
					_createUADConstants(uadApplicationName);
				}

				_deleteOrmXml();
				_deleteSpringLegacyXml();
			}
		}
		catch (FileNotFoundException fileNotFoundException) {
			System.out.println(fileNotFoundException.getMessage());
		}
	}

	public String formatPlural(String s) {
		if (isVersionLTE_7_2_0()) {
			if (Validator.isNull(s)) {
				return s;
			}

			if (s.endsWith("s")) {
				s = s.substring(0, s.length() - 1) + "ses";
			}
			else if (s.endsWith("y")) {
				s = s.substring(0, s.length() - 1) + "ies";
			}
			else {
				s = s + "s";
			}

			return s;
		}

		return TextFormatter.formatPlural(s);
	}

	public String getCacheFieldMethodName(JavaField javaField) {
		List<JavaAnnotation> javaAnnotations = javaField.getAnnotations();

		for (JavaAnnotation javaAnnotation : javaAnnotations) {
			JavaClass type = javaAnnotation.getType();

			String className = type.getFullyQualifiedName();

			if (className.equals(CacheField.class.getName())) {
				String methodName = null;

				Object namedParameter = javaAnnotation.getNamedParameter(
					"methodName");

				if (namedParameter != null) {
					methodName = StringUtil.unquote(
						StringUtil.trim(namedParameter.toString()));
				}

				if (Validator.isNull(methodName)) {
					methodName = TextFormatter.format(
						getVariableName(javaField), TextFormatter.G);
				}

				return methodName;
			}
		}

		throw new IllegalArgumentException(javaField + " is not a cache field");
	}

	public String getClassName(
		DefaultJavaParameterizedType defaultJavaParameterizedType) {

		int dimensions = defaultJavaParameterizedType.getDimensions();
		String name = defaultJavaParameterizedType.getFullyQualifiedName();

		if (dimensions == 0) {
			return name;
		}

		StringBundler sb = new StringBundler();

		for (int i = 0; i < dimensions; i++) {
			sb.append("[");
		}

		if (name.equals("boolean")) {
			sb.append("Z");
		}
		else if (name.equals("byte")) {
			sb.append("B");
		}
		else if (name.equals("char")) {
			sb.append("C");
		}
		else if (name.equals("double")) {
			sb.append("D");
		}
		else if (name.equals("float")) {
			sb.append("F");
		}
		else if (name.equals("int")) {
			sb.append("I");
		}
		else if (name.equals("long")) {
			sb.append("J");
		}
		else if (name.equals("short")) {
			sb.append("S");
		}
		else {
			sb.append("L");
			sb.append(name);
			sb.append(";");
		}

		return sb.toString();
	}

	public String getCompatJavaClassName(String key) {
		return _compatProperties.getProperty(
			StringBundler.concat(
				"java.class.name[", key, StringPool.CLOSE_BRACKET));
	}

	public String getCreateMappingTableSQL(EntityMapping entityMapping)
		throws Exception {

		String createMappingTableSQL = _getCreateMappingTableSQL(entityMapping);

		createMappingTableSQL = StringUtil.replace(
			createMappingTableSQL, '\n', "");
		createMappingTableSQL = StringUtil.replace(
			createMappingTableSQL, '\t', "");
		createMappingTableSQL = createMappingTableSQL.substring(
			0, createMappingTableSQL.length() - 1);

		return createMappingTableSQL;
	}

	public String getCreateTableSQL(Entity entity) {
		String createTableSQL = _getCreateTableSQL(entity);

		createTableSQL = StringUtil.replace(createTableSQL, '\n', "");
		createTableSQL = StringUtil.replace(createTableSQL, '\t', "");
		createTableSQL = createTableSQL.substring(
			0, createTableSQL.length() - 1);

		return createTableSQL;
	}

	public String getDimensions(int dims) {
		String dimensions = "";

		for (int i = 0; i < dims; i++) {
			dimensions += "[]";
		}

		return dimensions;
	}

	public String getDimensions(String dims) {
		return getDimensions(GetterUtil.getInteger(dims));
	}

	public String getDuplicateEntityExternalReferenceCodeException(
		Entity entity) {

		return "Duplicate" + _getEntityExceptionName(entity, false) +
			"ExternalReferenceCode";
	}

	public Entity getEntity(String name) throws Exception {
		Entity entity = _entityPool.get(name);

		if (entity != null) {
			return entity;
		}

		int pos = name.lastIndexOf(".");

		if (pos == -1) {
			pos = _entities.indexOf(new Entity(this, name));

			if (pos == -1) {
				throw new ServiceBuilderException(
					StringBundler.concat(
						"Unable to find ", name, " in ",
						ListUtil.toString(_entities, Entity.NAME_ACCESSOR)));
			}

			entity = _entities.get(pos);

			_entityPool.put(name, entity);

			return entity;
		}

		String refPackage = name.substring(0, pos);
		String refEntity = name.substring(pos + 1);

		if (refPackage.equals(_packagePath)) {
			pos = _entities.indexOf(new Entity(this, refEntity));

			if (pos == -1) {
				throw new ServiceBuilderException(
					StringBundler.concat(
						"Unable to find ", refEntity, " in ",
						ListUtil.toString(_entities, Entity.NAME_ACCESSOR)));
			}

			entity = _entities.get(pos);

			_entityPool.put(name, entity);

			return entity;
		}

		Set<Entity> entities = new HashSet<>(_entities);

		entities.addAll(_entityPool.values());

		for (Entity curEntity : entities) {
			if (refPackage.equals(curEntity.getApiPackagePath())) {
				refPackage = curEntity.getPackagePath();

				break;
			}
		}

		String refPackageDirName = StringUtil.replace(refPackage, '.', '/');

		String refFileName = StringBundler.concat(
			_implDirName, "/", refPackageDirName, "/service.xml");

		File refFile = new File(refFileName);

		boolean useTempFile = false;

		if (!refFile.exists()) {
			refFileName = String.valueOf(System.currentTimeMillis());

			refFile = new File(_TMP_DIR_NAME, refFileName);

			Class<?> clazz = getClass();

			ClassLoader classLoader = clazz.getClassLoader();

			String refContent = null;

			try {
				refContent = StringUtil.read(
					classLoader, refPackageDirName + "/service.xml");
			}
			catch (IOException ioException) {
				throw new ServiceBuilderException(
					StringBundler.concat(
						"Unable to find ", refEntity, " in ",
						ListUtil.toString(_entities, Entity.NAME_ACCESSOR)),
					ioException);
			}

			_write(refFile, refContent);

			useTempFile = true;
		}

		ServiceBuilder serviceBuilder = new ServiceBuilder(
			_apiDirName, _autoImportDefaultReferences, _autoNamespaceTables,
			_beanLocatorUtil, _buildNumber, _buildNumberIncrement,
			_databaseNameMaxLength, _hbmFileName, _implDirName,
			_incubationFeatures, refFile.getAbsolutePath(), _modelHintsFileName,
			_osgiModule, _pluginName, _propsUtil, _readOnlyPrefixes,
			_resourceActionModels, _resourcesDirName, _springFileName,
			_springNamespaces, _sqlDirName, _sqlFileName, _sqlIndexesFileName,
			_sqlSequencesFileName, _targetEntityName, _testDirName, _uadDirName,
			false);

		entity = serviceBuilder.getEntity(refEntity);

		entity.setPortalReference(useTempFile);

		_entityPool.put(name, entity);

		_modifiedFileNames.addAll(serviceBuilder.getModifiedFileNames());

		if (useTempFile) {
			refFile.deleteOnExit();
		}

		return entity;
	}

	public Entity getEntityByGenericsName(String genericsName) {
		try {
			String name = genericsName;

			if (name.startsWith("<")) {
				name = name.substring(1, name.length() - 1);
			}

			name = StringUtil.replace(name, ".model.", ".");

			return getEntity(name);
		}
		catch (Exception exception) {
			return null;
		}
	}

	public Entity getEntityByParameterTypeValue(String parameterTypeValue) {
		try {
			String name = parameterTypeValue;

			name = StringUtil.replace(name, ".model.", ".");

			return getEntity(name);
		}
		catch (Exception exception) {
			return null;
		}
	}

	public EntityMapping getEntityMapping(String mappingTable) {
		return _entityMappings.get(mappingTable);
	}

	public String getGeneratorClass(String idType) {
		if (Validator.isNull(idType)) {
			idType = "assigned";
		}

		return idType;
	}

	public String getGenericValue(JavaClass javaClass) {
		return StringUtil.replace(javaClass.getFullyQualifiedName(), '$', '.');
	}

	public String getJavadocComment(JavaClass javaClass) {
		return _formatComment(
			javaClass.getComment(), javaClass.getTags(), StringPool.BLANK);
	}

	public String getJavadocComment(JavaMethod javaMethod) {
		return _formatComment(
			javaMethod.getComment(), javaMethod.getTags(), StringPool.TAB);
	}

	public String getListActualTypeArguments(
		DefaultJavaParameterizedType defaultJavaParameterizedType) {

		String typeName = defaultJavaParameterizedType.getFullyQualifiedName();

		if (typeName.equals("java.util.List")) {
			List<JavaType> types =
				defaultJavaParameterizedType.getActualTypeArguments();

			if (types != null) {
				return getTypeGenericsName(types.get(0));
			}
		}

		return getTypeGenericsName(defaultJavaParameterizedType);
	}

	public String getLiteralClass(
		DefaultJavaParameterizedType defaultJavaParameterizedType) {

		StringBundler sb = new StringBundler(
			defaultJavaParameterizedType.getDimensions() + 2);

		sb.append(defaultJavaParameterizedType.getFullyQualifiedName());

		sb.append(".class");

		return sb.toString();
	}

	public Map<String, List<EntityColumn>> getMappingEntities(
			String mappingTable)
		throws Exception {

		Map<String, List<EntityColumn>> mappingEntities = new LinkedHashMap<>();

		EntityMapping entityMapping = _entityMappings.get(mappingTable);

		for (int i = 0; i < 3; i++) {
			Entity entity = getEntity(entityMapping.getEntityName(i));

			if (entity == null) {
				return null;
			}

			mappingEntities.put(entity.getName(), entity.getPKEntityColumns());
		}

		return mappingEntities;
	}

	public int getMaxLength(String model, EntityColumn entityColumn) {
		Map<String, String> hints = ModelHintsUtil.getHints(
			_apiPackagePath + ".model." + model,
			entityColumn.getModelHintsName());

		if (hints == null) {
			return _DEFAULT_COLUMN_MAX_LENGTH;
		}

		return GetterUtil.getInteger(
			hints.get("max-length"), _DEFAULT_COLUMN_MAX_LENGTH);
	}

	public Set<String> getModifiedFileNames() {
		return _modifiedFileNames;
	}

	public String getNoSuchEntityException(Entity entity) {
		return "NoSuch" +
			_getEntityExceptionName(entity, _shortNoSuchExceptionEnabled);
	}

	public String getParameterType(JavaParameter parameter) {
		JavaType returnType = parameter.getType();

		return getTypeGenericsName(returnType);
	}

	public String getPrimitiveObj(String type) {
		if (type.equals("boolean")) {
			return "Boolean";
		}
		else if (type.equals("double")) {
			return "Double";
		}
		else if (type.equals("float")) {
			return "Float";
		}
		else if (type.equals("int")) {
			return "Integer";
		}
		else if (type.equals("long")) {
			return "Long";
		}
		else if (type.equals("short")) {
			return "Short";
		}

		return type;
	}

	public String getPrimitiveObjValue(String colType) {
		if (colType.equals("Boolean")) {
			return ".booleanValue()";
		}
		else if (colType.equals("Double")) {
			return ".doubleValue()";
		}
		else if (colType.equals("Float")) {
			return ".floatValue()";
		}
		else if (colType.equals("Integer")) {
			return ".intValue()";
		}
		else if (colType.equals("Long")) {
			return ".longValue()";
		}
		else if (colType.equals("Short")) {
			return ".shortValue()";
		}

		return StringPool.BLANK;
	}

	public String getPrimitiveType(String type) {
		if (type.equals("Boolean")) {
			return "boolean";
		}
		else if (type.equals("Double")) {
			return "double";
		}
		else if (type.equals("Float")) {
			return "float";
		}
		else if (type.equals("Integer")) {
			return "int";
		}
		else if (type.equals("Long")) {
			return "long";
		}
		else if (type.equals("Short")) {
			return "short";
		}

		return type;
	}

	public String getReturnType(JavaMethod method) {
		return getTypeGenericsName(method.getReturnType());
	}

	public List<String> getServiceBaseExceptions(
		List<JavaMethod> methods, String methodName, List<String> args,
		List<String> exceptions) {

		boolean foundMethod = false;

		for (JavaMethod method : methods) {
			List<JavaParameter> parameters = method.getParameters();

			String curMethodName = method.getName();

			if (curMethodName.equals(methodName) &&
				(parameters.size() == args.size())) {

				for (int i = 0; i < parameters.size(); i++) {
					JavaParameter parameter = parameters.get(i);

					if (!Objects.equals(
							getParameterType(parameter), args.get(i))) {

						continue;
					}

					exceptions = ListUtil.copy(exceptions);

					List<JavaClass> methodExceptions = method.getExceptions();

					for (JavaClass methodException : methodExceptions) {
						String exception = methodException.getValue();

						if (exception.equals(PortalException.class.getName())) {
							exception = "PortalException";
						}

						if (exception.equals(SystemException.class.getName())) {
							exception = "SystemException";
						}

						if (!exceptions.contains(exception)) {
							exceptions.add(exception);
						}
					}

					Collections.sort(exceptions);

					foundMethod = true;

					break;
				}
			}

			if (foundMethod) {
				break;
			}
		}

		if (!exceptions.isEmpty()) {
			return exceptions;
		}

		return Collections.emptyList();
	}

	public String getSqlType(String type) {
		if (type.equals("boolean") || type.equals("Boolean")) {
			return "BOOLEAN";
		}
		else if (type.equals("double") || type.equals("Double")) {
			return "DOUBLE";
		}
		else if (type.equals("float") || type.equals("Float")) {
			return "FLOAT";
		}
		else if (type.equals("int") || type.equals("Integer")) {
			return "INTEGER";
		}
		else if (type.equals("long") || type.equals("Long")) {
			return "BIGINT";
		}
		else if (type.equals("short") || type.equals("Short")) {
			return "INTEGER";
		}
		else if (type.equals("BigDecimal")) {
			return "DECIMAL";
		}
		else if (type.equals("Date")) {
			return "TIMESTAMP";
		}
		else if (type.equals("String")) {
			return "VARCHAR";
		}

		return null;
	}

	public String getSqlType(String model, EntityColumn entityColumn) {
		String type = entityColumn.getType();

		if (type.equals("boolean") || type.equals("Boolean")) {
			return "BOOLEAN";
		}
		else if (type.equals("double") || type.equals("Double")) {
			return "DOUBLE";
		}
		else if (type.equals("float") || type.equals("Float")) {
			return "FLOAT";
		}
		else if (type.equals("int") || type.equals("Integer")) {
			return "INTEGER";
		}
		else if (type.equals("long") || type.equals("Long")) {
			return "BIGINT";
		}
		else if (type.equals("short") || type.equals("Short")) {
			return "INTEGER";
		}
		else if (type.equals("BigDecimal")) {
			return "DECIMAL";
		}
		else if (type.equals("Blob")) {
			return "BLOB";
		}
		else if (type.equals("Date")) {
			return "TIMESTAMP";
		}
		else if (type.equals("Map")) {
			return "CLOB";
		}
		else if (type.equals("String")) {
			int maxLength = getMaxLength(model, entityColumn);

			if (maxLength == 2000000) {
				return "CLOB";
			}

			return "VARCHAR";
		}

		return null;
	}

	public String getTypeGenericsName(JavaType javaType) {
		if (!(javaType instanceof DefaultJavaParameterizedType)) {
			return javaType.getFullyQualifiedName();
		}

		DefaultJavaParameterizedType defaultJavaParameterizedType =
			(DefaultJavaParameterizedType)javaType;

		List<JavaType> actualTypeArguments =
			defaultJavaParameterizedType.getActualTypeArguments();

		if (ListUtil.isEmpty(actualTypeArguments)) {
			return javaType.getFullyQualifiedName();
		}

		StringBundler sb = new StringBundler();

		sb.append(javaType.getFullyQualifiedName());

		sb.append(StringPool.LESS_THAN);

		for (JavaType actualTypeArgument : actualTypeArguments) {
			sb.append(getTypeGenericsName(actualTypeArgument));

			sb.append(StringPool.COMMA_AND_SPACE);
		}

		sb.setIndex(sb.index() - 1);

		sb.append(StringPool.GREATER_THAN);

		sb.append(getDimensions(defaultJavaParameterizedType.getDimensions()));

		return sb.toString();
	}

	public String getTypeParametersDefinition(
		List<DefaultJavaTypeVariable<?>> defaultJavaTypeVariables) {

		if (ListUtil.isEmpty(defaultJavaTypeVariables)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler();

		sb.append(StringPool.LESS_THAN);

		for (DefaultJavaTypeVariable<?> defaultJavaTypeVariable :
				defaultJavaTypeVariables) {

			sb.append(defaultJavaTypeVariable.getFullyQualifiedName());

			List<JavaType> javaTypes = defaultJavaTypeVariable.getBounds();

			if (ListUtil.isNotEmpty(javaTypes)) {
				sb.append(" extends ");

				for (JavaType javaType : javaTypes) {
					sb.append(javaType.getFullyQualifiedName());
					sb.append(StringPool.SPACE);
					sb.append(StringPool.AMPERSAND);
					sb.append(StringPool.SPACE);
				}

				sb.setIndex(sb.index() - 3);
			}

			sb.append(StringPool.COMMA_AND_SPACE);
		}

		sb.setStringAt(StringPool.GREATER_THAN, sb.index() - 1);

		return sb.toString();
	}

	public String getVariableName(JavaField field) {
		String fieldName = field.getName();

		if ((fieldName.length() > 0) && (fieldName.charAt(0) == '_')) {
			fieldName = fieldName.substring(1);
		}

		return fieldName;
	}

	public boolean hasEntityByGenericsName(String genericsName) {
		if (Validator.isNull(genericsName) ||
			!genericsName.contains(".model.") ||
			(getEntityByGenericsName(genericsName) == null)) {

			return false;
		}

		return true;
	}

	public boolean hasEntityByParameterTypeValue(String parameterTypeValue) {
		if (Validator.isNull(parameterTypeValue) ||
			!parameterTypeValue.contains(".model.") ||
			(getEntityByParameterTypeValue(parameterTypeValue) == null)) {

			return false;
		}

		return true;
	}

	public boolean isBasePersistenceMethod(JavaMethod method) {
		String methodName = method.getName();

		if (methodName.equals("clearCache") ||
			methodName.equals("fetchByPrimaryKeys") ||
			methodName.equals("findWithDynamicQuery") ||
			methodName.equals("setConfiguration") ||
			methodName.equals("setDataSource") ||
			methodName.equals("setSessionFactory")) {

			return true;
		}
		else if (methodName.equals("getBadColumnNames")) {
			return !isVersionLTE_7_1_0();
		}
		else if (methodName.equals("findByPrimaryKey") ||
				 methodName.equals("fetchByPrimaryKey") ||
				 methodName.equals("remove")) {

			List<JavaParameter> parameters = method.getParameters();

			if (parameters.size() == 1) {
				JavaParameter parameter = parameters.get(0);

				String parameterName = parameter.getName();

				if (parameterName.equals("primaryKey")) {
					return true;
				}
			}

			if (methodName.equals("remove")) {
				List<JavaClass> methodExceptions = method.getExceptions();

				for (JavaClass methodException : methodExceptions) {
					String exception = methodException.getValue();

					if (exception.contains("NoSuch")) {
						return false;
					}
				}

				return true;
			}
		}

		return false;
	}

	public boolean isCacheFieldPermanent(JavaField javaField) {
		if (isVersionLTE_7_3_0()) {
			return false;
		}

		List<JavaAnnotation> javaAnnotations = javaField.getAnnotations();

		for (JavaAnnotation javaAnnotation : javaAnnotations) {
			JavaClass type = javaAnnotation.getType();

			String className = type.getFullyQualifiedName();

			if (!className.equals(CacheField.class.getName())) {
				continue;
			}

			return GetterUtil.getBoolean(
				javaAnnotation.getNamedParameter("permanent"));
		}

		throw new IllegalArgumentException(javaField + " is not a cache field");
	}

	public boolean isCustomMethod(JavaMethod method) {
		String methodName = method.getName();

		if (methodName.equals("activate") ||
			methodName.equals("afterPropertiesSet") ||
			methodName.equals("clearService") ||
			methodName.equals("deactivate") || methodName.equals("destroy") ||
			methodName.equals("equals") ||
			methodName.equals("getAopInterfaces") ||
			methodName.equals("getCTColumnNames") ||
			methodName.equals("getCTPersistence") ||
			methodName.equals("getClass") ||
			methodName.equals("getMappingTableNames") ||
			methodName.equals("getModelClass") ||
			methodName.equals("getService") ||
			methodName.equals("getTableColumnsMap") ||
			methodName.equals("getTableName") ||
			methodName.equals("getUniqueIndexColumnNames") ||
			methodName.equals("getWrappedService") ||
			methodName.equals("hashCode") || methodName.equals("notify") ||
			methodName.equals("notifyAll") ||
			methodName.equals("setAopProxy") ||
			methodName.equals("setWrappedService") ||
			methodName.equals("toString") ||
			methodName.equals("updateWithUnsafeFunction") ||
			methodName.equals("wait")) {

			return false;
		}
		else if (methodName.equals("getPermissionChecker")) {
			return false;
		}
		else if (methodName.equals("getUser") ||
				 methodName.equals("getUserId")) {

			List<JavaParameter> parameters = method.getParameters();

			if (parameters.isEmpty()) {
				return false;
			}
		}

		JavaClass javaClass = method.getDeclaringClass();

		String packageName = javaClass.getPackageName();

		if (!packageName.endsWith(".service.base")) {
			return true;
		}

		if (!methodName.endsWith("Finder") &&
			!methodName.endsWith("Persistence") &&
			!methodName.endsWith("Service")) {

			return true;
		}

		JavaType javaType = null;

		List<JavaType> parameterTypes = method.getParameterTypes(true);

		if (methodName.startsWith("get")) {
			if (ListUtil.isEmpty(parameterTypes)) {
				javaType = method.getReturnType(true);
			}
		}
		else if (methodName.startsWith("set")) {
			if ((parameterTypes != null) && (parameterTypes.size() == 1)) {
				javaType = parameterTypes.get(0);
			}
		}

		if (javaType == null) {
			return true;
		}

		String typeClassName = javaType.getFullyQualifiedName();

		int index = typeClassName.lastIndexOf(CharPool.PERIOD);

		if (index == -1) {
			return true;
		}

		String typePackageName = typeClassName.substring(0, index);

		if (typePackageName.endsWith(".persistence") ||
			typePackageName.endsWith(".service")) {

			return false;
		}

		return true;
	}

	public boolean isDSLEnabled() {
		if (isVersionGTE_7_4_0() ||
			ArrayUtil.contains(_incubationFeatures, "DSL")) {

			return true;
		}

		return false;
	}

	public boolean isHBMCamelCasePropertyAccessor(String propertyName) {
		if (propertyName.length() < 3) {
			return false;
		}

		char[] chars = propertyName.toCharArray();

		char c0 = chars[0];
		char c1 = chars[1];
		char c2 = chars[2];

		if (Character.isLowerCase(c0) && Character.isUpperCase(c1) &&
			Character.isLowerCase(c2)) {

			return true;
		}

		return false;
	}

	public boolean isReadOnlyMethod(
		JavaMethod javaMethod, List<String> txRequiredMethodNames,
		String[] prefixes) {

		List<JavaAnnotation> javaAnnotations = javaMethod.getAnnotations();

		if (javaAnnotations != null) {
			for (JavaAnnotation javaAnnotation : javaAnnotations) {
				JavaClass type = javaAnnotation.getType();

				String className = type.getFullyQualifiedName();

				if (className.equals(Transactional.class.getName())) {
					return false;
				}
			}
		}

		String methodName = javaMethod.getName();

		if (isTxRequiredMethod(javaMethod, txRequiredMethodNames)) {
			return false;
		}

		for (String prefix : prefixes) {
			if (methodName.startsWith(prefix)) {
				return true;
			}
		}

		return false;
	}

	public boolean isServiceReadOnlyMethod(
		JavaMethod method, List<String> txRequiredMethodNames) {

		return isReadOnlyMethod(
			method, txRequiredMethodNames, _readOnlyPrefixes);
	}

	public boolean isSoapMethod(JavaMethod method) {
		JavaType returnType = method.getReturnType();

		String returnTypeGenericsName = getTypeGenericsName(returnType);
		String returnValueName = returnType.getFullyQualifiedName();

		if (returnTypeGenericsName.contains(
				"com.liferay.portal.kernel.search.") ||
			returnTypeGenericsName.contains(
				"com.liferay.portal.kernel.model.Theme") ||
			returnTypeGenericsName.contains(
				"com.liferay.social.kernel.model.SocialActivityDefinition") ||
			returnTypeGenericsName.equals("java.util.List<java.lang.Object>") ||
			returnValueName.equals(
				"com.liferay.portal.kernel.lock.model.Lock") ||
			returnValueName.equals(
				"com.liferay.message.boards.kernel.model.MBMessageDisplay") ||
			returnValueName.equals(
				"com.liferay.message.boards.model.MBMessageDisplay") ||
			returnValueName.startsWith("java.io") ||
			returnValueName.equals("java.util.Map") ||
			returnValueName.equals("java.util.Properties") ||
			returnValueName.startsWith("javax")) {

			return false;
		}

		if (returnTypeGenericsName.contains(
				"com.liferay.portal.kernel.repository.model.FileEntry") ||
			returnTypeGenericsName.contains(
				"com.liferay.portal.kernel.repository.model.Folder")) {
		}
		else if (returnTypeGenericsName.contains(
					"com.liferay.portal.kernel.repository.")) {

			return false;
		}

		List<JavaParameter> parameters = method.getParameters();

		for (JavaParameter javaParameter : parameters) {
			JavaType type = javaParameter.getType();

			String parameterTypeName = type.getFullyQualifiedName();

			if (parameterTypeName.equals(
					"com.liferay.portal.kernel.util.UnicodeProperties") ||
				parameterTypeName.equals(
					"com.liferay.portal.kernel.theme.ThemeDisplay") ||
				parameterTypeName.equals(
					"com.liferay.portlet.PortletPreferencesImpl") ||
				parameterTypeName.equals(
					"com.liferay.portlet.dynamicdatamapping.Fields") ||
				parameterTypeName.startsWith("java.io") ||
				parameterTypeName.startsWith("java.util.LinkedHashMap") ||
				//parameterTypeName.startsWith("java.util.List") ||
				//parameterTypeName.startsWith("java.util.Locale") ||
				(parameterTypeName.startsWith("java.util.Map") &&
				 !_isStringLocaleMap(javaParameter)) ||
				parameterTypeName.startsWith("java.util.Properties") ||
				parameterTypeName.startsWith("javax")) {

				return false;
			}
		}

		return true;
	}

	public boolean isTxRequiredMethod(
		JavaMethod javaMethod, List<String> txRequiredMethodNames) {

		if (txRequiredMethodNames == null) {
			return false;
		}

		return txRequiredMethodNames.contains(javaMethod.getName());
	}

	public boolean isVersionGTE_7_0_0() {
		if (_dtdVersion.isLaterVersionThan("7.0.0") ||
			_dtdVersion.isSameVersionAs("7.0.0")) {

			return true;
		}

		return false;
	}

	public boolean isVersionGTE_7_1_0() {
		if (_dtdVersion.isLaterVersionThan("7.1.0") ||
			_dtdVersion.isSameVersionAs("7.1.0")) {

			return true;
		}

		return false;
	}

	public boolean isVersionGTE_7_2_0() {
		if (_dtdVersion.isLaterVersionThan("7.2.0") ||
			_dtdVersion.isSameVersionAs("7.2.0")) {

			return true;
		}

		return false;
	}

	public boolean isVersionGTE_7_3_0() {
		if (_dtdVersion.isLaterVersionThan("7.3.0") ||
			_dtdVersion.isSameVersionAs("7.3.0")) {

			return true;
		}

		return false;
	}

	public boolean isVersionGTE_7_4_0() {
		if (_dtdVersion.isLaterVersionThan("7.4.0") ||
			_dtdVersion.isSameVersionAs("7.4.0")) {

			return true;
		}

		return false;
	}

	public boolean isVersionLTE_7_1_0() {
		if (_dtdVersion.isPreviousVersionThan("7.1.0") ||
			_dtdVersion.isSameVersionAs("7.1.0")) {

			return true;
		}

		return false;
	}

	public boolean isVersionLTE_7_2_0() {
		if (_dtdVersion.isPreviousVersionThan("7.2.0") ||
			_dtdVersion.isSameVersionAs("7.2.0")) {

			return true;
		}

		return false;
	}

	public boolean isVersionLTE_7_3_0() {
		if (_dtdVersion.isPreviousVersionThan("7.3.0") ||
			_dtdVersion.isSameVersionAs("7.3.0")) {

			return true;
		}

		return false;
	}

	public String javaAnnotationToString(JavaAnnotation javaAnnotation) {
		StringBundler sb = new StringBundler();

		sb.append(StringPool.AT);

		JavaClass type = javaAnnotation.getType();

		sb.append(type.getFullyQualifiedName());

		Map<String, Object> namedParameters =
			javaAnnotation.getNamedParameterMap();

		if (namedParameters.isEmpty()) {
			return sb.toString();
		}

		sb.append(StringPool.OPEN_PARENTHESIS);

		for (Map.Entry<String, Object> entry : namedParameters.entrySet()) {
			sb.append(entry.getKey());

			sb.append(StringPool.EQUAL);

			Object value = entry.getValue();

			if (value instanceof List) {
				List<?> values = (List<?>)value;

				sb.append(StringPool.OPEN_CURLY_BRACE);

				for (Object object : values) {
					if (object instanceof JavaAnnotation) {
						sb.append(
							javaAnnotationToString((JavaAnnotation)object));
					}
					else {
						sb.append(object);
					}

					sb.append(StringPool.COMMA_AND_SPACE);
				}

				if (!values.isEmpty()) {
					sb.setIndex(sb.index() - 1);
				}

				sb.append(StringPool.CLOSE_CURLY_BRACE);
			}
			else {
				sb.append(value);
			}

			sb.append(StringPool.COMMA_AND_SPACE);
		}

		sb.setIndex(sb.index() - 1);

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	private static SAXReader _getSAXReader() {
		return SAXReaderFactory.getSAXReader(null, false, false);
	}

	private static void _readResourceActionModels(
			String implDirName, String resourcesDirName,
			InputStream inputStream, Set<String> resourceActionModels)
		throws Exception {

		SAXReader saxReader = _getSAXReader();

		Document document = saxReader.read(inputStream);

		Element rootElement = document.getRootElement();

		List<Element> resourceElements = rootElement.elements("resource");

		for (Element resourceElement : resourceElements) {
			resourceActionModels.addAll(
				readResourceActionModels(
					implDirName, resourcesDirName,
					new String[] {resourceElement.attributeValue("file")}));
		}

		XPath xPath = document.createXPath("//model-resource/model-name");

		List<Node> nodes = xPath.selectNodes(rootElement);

		for (Node node : nodes) {
			resourceActionModels.add(StringUtil.trim(node.getText()));
		}
	}

	private void _addIndexMetadata(
		List<IndexMetadata> indexMetadatas, IndexMetadata indexMetadata) {

		Iterator<IndexMetadata> iterator = indexMetadatas.iterator();

		while (iterator.hasNext()) {
			IndexMetadata currentIndexMetadata = iterator.next();

			Boolean redundant = currentIndexMetadata.redundantTo(indexMetadata);

			if (redundant == null) {
				continue;
			}

			if (redundant) {
				iterator.remove();
			}
			else {
				indexMetadata = null;

				break;
			}
		}

		if (indexMetadata != null) {
			indexMetadatas.add(indexMetadata);
		}
	}

	private void _addIndexMetadata(
		Map<String, List<IndexMetadata>> indexMetadatasMap, String tableName,
		List<String> pkEntityColumnDBNames, IndexMetadata indexMetadata) {

		if ((pkEntityColumnDBNames != null) &&
			!pkEntityColumnDBNames.isEmpty()) {

			String[] columnNames = indexMetadata.getColumnNames();

			if (columnNames.length <= pkEntityColumnDBNames.size()) {
				if (pkEntityColumnDBNames.size() > 1) {
					boolean redundant = true;

					for (int i = 0; i < columnNames.length; i++) {
						if (!columnNames[i].equals(
								pkEntityColumnDBNames.get(i))) {

							redundant = false;

							break;
						}
					}

					if (redundant) {
						return;
					}
				}
			}
			else if (indexMetadata.isUnique()) {
				boolean redundant = true;

				for (String pkEntityColumnDBName : pkEntityColumnDBNames) {
					if (!ArrayUtil.contains(
							columnNames, pkEntityColumnDBName)) {

						redundant = false;

						break;
					}
				}

				if (redundant) {
					return;
				}
			}
		}

		List<IndexMetadata> indexMetadatas = indexMetadatasMap.computeIfAbsent(
			tableName, key -> new ArrayList<>());

		if (_optimizeDBIndexes) {
			indexMetadatas.add(indexMetadata);
		}
		else {
			_addIndexMetadata(indexMetadatas, indexMetadata);
		}
	}

	private boolean _containSpecialCharacter(String name) {
		for (char c : name.toCharArray()) {
			if (((c >= CharPool.LOWER_CASE_A) &&
				 (c <= CharPool.LOWER_CASE_Z)) ||
				((c >= CharPool.UPPER_CASE_A) &&
				 (c <= CharPool.UPPER_CASE_Z)) ||
				((c >= CharPool.NUMBER_0) && (c <= CharPool.NUMBER_9)) ||
				(c == CharPool.UNDERLINE)) {

				continue;
			}

			return true;
		}

		return false;
	}

	private void _createBaseUADAnonymizer(Entity entity) throws Exception {
		Map<String, Object> context = _getContext();

		context.put(
			"deleteUADEntityMethodName",
			_getDeleteUADEntityMethodName(
				_getJavaClass(
					StringBundler.concat(
						_outputPath, "/service/impl/", entity.getName(),
						_getSessionTypeName(_SESSION_TYPE_LOCAL),
						"ServiceImpl.java")),
				entity.getName()));
		context.put("entity", entity);

		String content = _processTemplate(_TPL_BASE_UAD_ANONYMIZER, context);

		File file = new File(
			StringBundler.concat(
				entity.getUADOutputPath(), "/uad/anonymizer/Base",
				entity.getName(), "UADAnonymizer.java"));

		_write(file, content, _modifiedFileNames);
	}

	private void _createBaseUADDisplay(Entity entity) throws Exception {
		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		String content = _processTemplate(_TPL_BASE_UAD_DISPLAY, context);

		File file = new File(
			StringBundler.concat(
				entity.getUADOutputPath(), "/uad/display/Base",
				entity.getName(), "UADDisplay.java"));

		_write(file, content, _modifiedFileNames);
	}

	private void _createBaseUADExporter(Entity entity) throws Exception {
		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		String content = _processTemplate(_TPL_BASE_UAD_EXPORTER, context);

		File file = new File(
			StringBundler.concat(
				entity.getUADOutputPath(), "/uad/exporter/Base",
				entity.getName(), "UADExporter.java"));

		_write(file, content, _modifiedFileNames);
	}

	private void _createBlobModels(Entity entity) throws Exception {
		List<EntityColumn> blobEntityColumns = _getBlobEntityColumns(entity);

		if (blobEntityColumns.isEmpty()) {
			return;
		}

		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		for (EntityColumn blobEntityColumn : blobEntityColumns) {
			context.put("column", blobEntityColumn);

			String content = _processTemplate(_tplBlobModel, context);

			File blobModelFile = new File(
				StringBundler.concat(
					_serviceOutputPath, "/model/", entity.getName(),
					blobEntityColumn.getMethodName(), "BlobModel.java"));

			_write(blobModelFile, content, _modifiedFileNames);
		}
	}

	private void _createCTServiceImpl(Entity entity) throws Exception {
		File file = new File(
			StringBundler.concat(
				_outputPath, "/service/impl/", entity.getName(),
				"CTServiceImpl.java"));

		if (!entity.isChangeTrackingEnabled() || !entity.hasEntityColumns() ||
			entity.hasLocalService()) {

			if (file.exists()) {
				System.out.println("Removing " + file);

				file.delete();
			}

			return;
		}

		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		String content = _processTemplate(_TPL_CT_SERVICE_IMPL, context);

		_write(file, content, _modifiedFileNames);
	}

	/**
	 * @see #_getCreateTableSQL(Entity)
	 */
	private void _createDSLTable(
			List<EntityColumn> entityColumns, String name, String tableName,
			boolean changeTrackingEnabled, boolean classDeprecated,
			String classDeprecatedComment, List<String> modelNames)
		throws Exception {

		if (!isDSLEnabled()) {
			return;
		}

		File modelTableFile = new File(
			StringBundler.concat(
				_serviceOutputPath, "/model/", name, "Table.java"));

		String content = _processTemplate(
			_TPL_MODEL_TABLE,
			HashMapBuilder.<String, Object>put(
				"apiPackagePath", _apiPackagePath
			).put(
				"author", _author
			).put(
				"changeTrackingEnabled", changeTrackingEnabled
			).put(
				"classDeprecated", classDeprecated
			).put(
				"classDeprecatedComment", classDeprecatedComment
			).put(
				"columns",
				() -> {
					List<Map<String, String>> columns = new ArrayList<>(
						entityColumns.size());

					for (EntityColumn entityColumn : entityColumns) {
						String sqlType = getSqlType(name, entityColumn);

						columns.add(
							HashMapBuilder.put(
								"dbName", entityColumn.getDBName()
							).put(
								"flag",
								() -> {
									if (entityColumn.isPrimary() ||
										(changeTrackingEnabled &&
										 Objects.equals(
											 entityColumn.getName(),
											 "ctCollectionId"))) {

										return "FLAG_PRIMARY";
									}

									if (Objects.equals(
											entityColumn.getName(),
											"mvccVersion")) {

										return "FLAG_NULLITY";
									}

									return "FLAG_DEFAULT";
								}
							).put(
								"javaType",
								() -> {
									if (entityColumn.isPrimitiveType()) {
										return getPrimitiveObj(
											entityColumn.getType());
									}

									if (Objects.equals(sqlType, "CLOB")) {
										return "Clob";
									}

									return entityColumn.getGenericizedType();
								}
							).put(
								"name", entityColumn.getName()
							).put(
								"sqlType", sqlType
							).build());
					}

					return columns;
				}
			).put(
				"modelNames", modelNames
			).put(
				"name", name
			).put(
				"table", tableName
			).build());

		_write(modelTableFile, content, _modifiedFileNames);
	}

	private void _createEJBPK(Entity entity) throws Exception {
		List<EntityColumn> pkEntityColumns = entity.getPKEntityColumns();

		if (pkEntityColumns.size() <= 1) {
			return;
		}

		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		String content = _processTemplate(_tplEjbPK, context);

		File file = new File(
			StringBundler.concat(
				_serviceOutputPath, "/service/persistence/",
				entity.getPKClassName(), ".java"));

		_write(file, content, _modifiedFileNames);
	}

	private void _createEntityMappingTable(EntityMapping entityMapping)
		throws Exception {

		List<EntityColumn> entityColumns = new ArrayList<>();

		boolean changeTrackingEnabled = true;
		boolean classDeprecated = false;
		String classDeprecatedComment = "";
		List<String> modelNames = new ArrayList<>(2);

		for (int i = 0; i < 3; i++) {
			Entity entity = getEntity(entityMapping.getEntityName(i));

			if (i > 0) {
				if (!entity.isChangeTrackingEnabled()) {
					changeTrackingEnabled = false;
				}

				modelNames.add(entity.getName());
			}

			if (entity.isDeprecated()) {
				classDeprecated = true;

				JavaClass modelJavaClass = _getJavaClass(
					StringBundler.concat(
						_serviceOutputPath, "/model/", entity.getName(),
						"Model.java"));

				if (modelJavaClass != null) {
					DocletTag tag = modelJavaClass.getTagByName("deprecated");

					if (tag != null) {
						classDeprecatedComment = tag.getValue();
					}
				}
			}

			entityColumns.addAll(entity.getPKEntityColumns());
		}

		if (changeTrackingEnabled) {
			entityColumns.add(
				new EntityColumn(
					this, "ctCollectionId", null, "ctCollectionId", null,
					"long", true, false, false, null, null, true, true, false,
					null, null, false, null, null, true, true, false, false,
					CTColumnResolutionType.STRICT, false, false, null, false));

			entityColumns.add(
				new EntityColumn(
					this, "ctChangeType", null, "ctChangeType", null, "boolean",
					false, false, false, null, null, true, true, false, null,
					null, false, null, null, true, true, false, false,
					CTColumnResolutionType.STRICT, false, false, null, false));
		}

		String name = entityMapping.getTableName();

		_createDSLTable(
			entityColumns, name, name, changeTrackingEnabled, classDeprecated,
			classDeprecatedComment, modelNames);
	}

	private void _createExceptions(List<String> exceptions) throws Exception {
		for (Entity entity : _entities) {
			if (!_isTargetEntity(entity)) {
				continue;
			}

			if (entity.hasEntityColumns()) {
				if ((entity.hasExternalReferenceCode() ||
					 entity.hasEntityColumn("externalReferenceCode")) &&
					(entity.getVersionedEntity() == null)) {

					exceptions.add(
						getDuplicateEntityExternalReferenceCodeException(
							entity));
				}

				exceptions.add(getNoSuchEntityException(entity));
			}
		}

		for (String exception : exceptions) {
			File oldExceptionFile = new File(
				StringBundler.concat(
					_oldServiceOutputPath, "/", exception, "Exception.java"));

			if (!oldExceptionFile.exists()) {
				oldExceptionFile = new File(
					StringBundler.concat(
						_oldServiceOutputPath, "/exception/", exception,
						"Exception.java"));
			}

			if (!oldExceptionFile.exists()) {
				oldExceptionFile = new File(
					StringBundler.concat(
						_serviceOutputPath, "/", exception, "Exception.java"));
			}

			File exceptionFile = new File(
				StringBundler.concat(
					_serviceOutputPath, "/exception/", exception,
					"Exception.java"));

			if (oldExceptionFile.exists() &&
				!oldExceptionFile.equals(exceptionFile)) {

				exceptionFile.delete();

				Files.createDirectories(
					Paths.get(_serviceOutputPath, "exception"));

				Files.move(oldExceptionFile.toPath(), exceptionFile.toPath());

				String content = _read(exceptionFile);

				content = StringUtil.replace(
					content,
					new String[] {
						"package " + _packagePath + ";",
						"package " + _packagePath + ".exception;",
						"package " + _apiPackagePath + ";",
						"com.liferay.portal.NoSuchModelException"
					},
					new String[] {
						"package " + _apiPackagePath + ".exception;",
						"package " + _apiPackagePath + ".exception;",
						"package " + _apiPackagePath + ".exception;",
						"com.liferay.portal.kernel.exception." +
							"NoSuchModelException"
					});

				_write(exceptionFile, content);
			}

			if (!exceptionFile.exists()) {
				Map<String, Object> context = _getContext();

				context.put("exception", exception);

				String content = _processTemplate(_tplException, context);

				if (exception.startsWith("Duplicate") &&
					exception.endsWith("ExternalReferenceCode")) {

					content = StringUtil.replace(
						content, "PortalException",
						isVersionGTE_7_4_0() ?
							"DuplicateExternalReferenceCodeException" :
								"SystemException");
				}
				else if (exception.startsWith("NoSuch")) {
					content = StringUtil.replace(
						content, "PortalException", "NoSuchModelException");
				}

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy");

				content = content.replaceFirst(
					Pattern.quote("{$year}"),
					simpleDateFormat.format(new Date()));

				content = StringUtil.replace(content, "\r\n", "\n");

				ToolsUtil.writeFileRaw(
					exceptionFile, content, _modifiedFileNames);
			}

			if (exception.startsWith("Duplicate") &&
				exception.endsWith("ExternalReferenceCode")) {

				String content = _read(exceptionFile);

				if (isVersionGTE_7_4_0()) {
					if (!content.contains(
							"DuplicateExternalReferenceCodeException")) {

						content = StringUtil.replace(
							content, "PortalException",
							"DuplicateExternalReferenceCodeException");
						content = StringUtil.replace(
							content, "SystemException",
							"DuplicateExternalReferenceCodeException");

						ToolsUtil.writeFileRaw(
							exceptionFile, content, _modifiedFileNames);
					}
				}
				else {
					if (!content.contains("SystemException")) {
						content = StringUtil.replace(
							content, "PortalException", "SystemException");

						ToolsUtil.writeFileRaw(
							exceptionFile, content, _modifiedFileNames);
					}
				}
			}
			else if (exception.startsWith("NoSuch")) {
				String content = _read(exceptionFile);

				if (!content.contains("NoSuchModelException")) {
					content = StringUtil.replace(
						content, "PortalException", "NoSuchModelException");
					content = StringUtil.replace(
						content, "portal.exception.NoSuchModelException",
						"portal.kernel.exception.NoSuchModelException");

					ToolsUtil.writeFileRaw(
						exceptionFile, content, _modifiedFileNames);
				}
				else if (content.contains(
							"portal.exception.NoSuchModelException")) {

					content = StringUtil.replace(
						content, "portal.exception.NoSuchModelException",
						"portal.kernel.exception.NoSuchModelException");

					ToolsUtil.writeFileRaw(
						exceptionFile, content, _modifiedFileNames);
				}
			}
		}
	}

	private void _createExtendedModel(Entity entity) throws Exception {
		JavaClass modelImplJavaClass = _getJavaClass(
			StringBundler.concat(
				_outputPath, "/model/impl/", entity.getName(), "Impl.java"));

		Map<String, JavaMethod> methods = new LinkedHashMap<>();

		for (JavaMethod method : _getMethods(modelImplJavaClass)) {
			methods.put(_getMethodSignature(method, false), method);
		}

		Set<Map.Entry<String, JavaMethod>> entrySet = methods.entrySet();

		Iterator<Map.Entry<String, JavaMethod>> iterator = entrySet.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, JavaMethod> entry = iterator.next();

			JavaMethod method = entry.getValue();

			String methodName = method.getName();

			if (methodName.equals("getStagedModelType")) {
				iterator.remove();
			}
		}

		JavaClass modelJavaClass = _getJavaClass(
			StringBundler.concat(
				_serviceOutputPath, "/model/", entity.getName(), "Model.java"));

		for (JavaMethod method : _getMethods(modelJavaClass)) {
			methods.remove(_getMethodSignature(method, false));
		}

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", methods.values());

		context = _putDeprecatedKeys(context, modelJavaClass);

		String content = _processTemplate(_tplExtendedModel, context);

		File modelFile = new File(
			StringBundler.concat(
				_serviceOutputPath, "/model/", entity.getName(), ".java"));

		_write(modelFile, content, _modifiedFileNames);
	}

	private void _createExtendedModelBaseImpl(Entity entity) throws Exception {
		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		JavaClass modelImplJavaClass = _getJavaClass(
			StringBundler.concat(
				_outputPath, "/model/impl/", entity.getName(), "Impl.java"));

		context = _putDeprecatedKeys(context, modelImplJavaClass);

		String content = _processTemplate(_tplExtendedModelBaseImpl, context);

		File modelFile = new File(
			StringBundler.concat(
				_outputPath, "/model/impl/", entity.getName(),
				"BaseImpl.java"));

		_write(modelFile, content, _modifiedFileNames);
	}

	private void _createExtendedModelImpl(Entity entity) throws Exception {
		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		String content = _processTemplate(_tplExtendedModelImpl, context);

		File modelFile = new File(
			StringBundler.concat(
				_outputPath, "/model/impl/", entity.getName(), "Impl.java"));

		if (modelFile.exists()) {
			content = _read(modelFile);

			content = content.replaceAll(
				StringBundler.concat(
					"extends\\s+", entity.getName(),
					"ModelImpl\\s+implements\\s+", entity.getName()),
				"extends " + entity.getName() + "BaseImpl");

			ToolsUtil.writeFileRaw(modelFile, content, _modifiedFileNames);
		}
		else {
			_write(modelFile, content, _modifiedFileNames);
		}
	}

	private void _createFinder(Entity entity) throws Exception {
		if (!entity.hasFinderClassName()) {
			_removeFinder(entity, _serviceOutputPath);

			return;
		}

		JavaClass javaClass = _getJavaClass(
			StringBundler.concat(
				_outputPath, "/service/persistence/impl/", entity.getName(),
				"FinderImpl.java"));

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", _getMethods(javaClass));

		context = _putDeprecatedKeys(context, javaClass);

		String content = _processTemplate(_tplFinder, context);

		File file = new File(
			StringBundler.concat(
				_serviceOutputPath, "/service/persistence/", entity.getName(),
				"Finder.java"));

		_write(file, content, _modifiedFileNames);
	}

	private void _createFinderBaseImpl(Entity entity) throws Exception {
		if (!entity.hasFinderClassName() ||
			_packagePath.equals("com.liferay.counter")) {

			_removeFinderBaseImpl(entity);

			return;
		}

		File finderImplFile = new File(
			StringBundler.concat(
				_outputPath, "/service/persistence/impl/", entity.getName(),
				"FinderImpl.java"));

		if (finderImplFile.exists()) {
			String content = _read(finderImplFile);

			content = StringUtil.removeSubstring(
				content,
				"import com.liferay.portal.service.persistence.impl." +
					"BasePersistenceImpl;\n");

			content = StringUtil.replace(
				content, "BasePersistenceImpl<" + entity.getName() + ">",
				entity.getName() + "FinderBaseImpl");

			ToolsUtil.writeFileRaw(finderImplFile, content, _modifiedFileNames);
		}

		JavaClass javaClass = _getJavaClass(finderImplFile.getPath());

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("persistence", Boolean.FALSE);

		context = _putDeprecatedKeys(context, javaClass);

		String content = _processTemplate(_tplFinderBaseImpl, context);

		File file = new File(
			StringBundler.concat(
				_outputPath, "/service/persistence/impl/", entity.getName(),
				"FinderBaseImpl.java"));

		_write(file, content, _modifiedFileNames);
	}

	private void _createFinderUtil(Entity entity) throws Exception {
		if (!entity.hasFinderClassName() || _osgiModule) {
			_removeFinderUtil(entity, _serviceOutputPath);

			return;
		}

		JavaClass javaClass = _getJavaClass(
			StringBundler.concat(
				_outputPath, "/service/persistence/impl/", entity.getName(),
				"FinderImpl.java"));

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", _getMethods(javaClass));

		context = _putDeprecatedKeys(context, javaClass);

		String content = _processTemplate(_tplFinderUtil, context);

		File file = new File(
			StringBundler.concat(
				_serviceOutputPath, "/service/persistence/", entity.getName(),
				"FinderUtil.java"));

		_write(file, content, _modifiedFileNames);
	}

	private void _createHbm(Entity entity) {
		File file = new File(
			StringBundler.concat(
				_outputPath, "/service/persistence/", entity.getName(),
				"HBM.java"));

		if (file.exists()) {
			System.out.println("Removing deprecated " + file);

			file.delete();
		}
	}

	private void _createHbmUtil(Entity entity) {
		File file = new File(
			StringBundler.concat(
				_outputPath, "/service/persistence/", entity.getName(),
				"HBMUtil.java"));

		if (file.exists()) {
			System.out.println("Removing deprecated " + file);

			file.delete();
		}
	}

	private void _createHbmXml() throws Exception {
		File xmlFile = new File(_hbmFileName);

		List<Entity> entities = new ArrayList<>();

		boolean hasDeprecated = false;

		for (Entity entity : _entities) {
			if (entity.hasEntityColumns() &&
				(entity.hasFinderClassName() || entity.hasPersistence())) {

				if (entity.isDeprecated()) {
					hasDeprecated = true;
				}
				else {
					entities.add(entity);
				}
			}
		}

		if (entities.isEmpty()) {
			if (!hasDeprecated) {
				System.out.println("Removing " + xmlFile);

				xmlFile.delete();
			}

			return;
		}

		Map<String, Object> context = _getContext();

		context.put("entities", entities);

		String content = _processTemplate(_tplHbmXml, context);

		int lastImportStart = content.lastIndexOf("<import class=");

		int lastImportEnd = content.indexOf("/>", lastImportStart) + 3;

		String imports = content.substring(0, lastImportEnd);

		content = content.substring(lastImportEnd + 1);

		if (!xmlFile.exists()) {
			String hbmNamespace = _HIBERNATE_3_HBM_NAMESPACE;

			if (isVersionGTE_7_4_0()) {
				hbmNamespace = _HIBERNATE_5_HBM_NAMESPACE;
			}

			String xml = StringBundler.concat(
				"<?xml version=\"1.0\"?>\n",
				"<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate ",
				"Mapping DTD 3.0//EN\" ", hbmNamespace, ">\n\n",
				"<hibernate-mapping auto-import=\"false\" default-lazy=",
				"\"false\">\n", "</hibernate-mapping>");

			_write(xmlFile, xml);
		}

		String oldContent = _read(xmlFile);

		String newContent = _fixHbmXml(oldContent);

		int firstImport = newContent.indexOf(
			"<import class=\"" + _packagePath + ".model.");
		int lastImport = newContent.lastIndexOf(
			"<import class=\"" + _packagePath + ".model.");

		if (firstImport == -1) {
			firstImport = newContent.indexOf(
				"<import class=\"" + _apiPackagePath + ".model.");
			lastImport = newContent.lastIndexOf(
				"<import class=\"" + _apiPackagePath + ".model.");
		}

		if (firstImport == -1) {
			int x = newContent.indexOf("<class");

			if (x != -1) {
				newContent =
					newContent.substring(0, x) + imports +
						newContent.substring(x);
			}
			else {
				content = imports + content;
			}
		}
		else {
			firstImport = newContent.indexOf("<import", firstImport) - 1;
			lastImport = newContent.indexOf("/>", lastImport) + 3;

			newContent =
				newContent.substring(0, firstImport) + imports +
					newContent.substring(lastImport);
		}

		int firstClass = newContent.lastIndexOf(
			"<class ",
			newContent.indexOf(" name=\"" + _packagePath + ".model.") - 6);

		int lastClass = newContent.lastIndexOf(
			"<class ",
			newContent.lastIndexOf(" name=\"" + _packagePath + ".model.") - 6);

		if (firstClass == -1) {
			int x = newContent.indexOf("</hibernate-mapping>");

			if (x != -1) {
				newContent =
					newContent.substring(0, x) + content +
						newContent.substring(x);
			}
		}
		else {
			firstClass = newContent.lastIndexOf("<class", firstClass) - 1;
			lastClass = newContent.indexOf("</class>", lastClass) + 9;

			newContent =
				newContent.substring(0, firstClass) + content +
					newContent.substring(lastClass);
		}

		ToolsUtil.writeFileRaw(
			xmlFile, _formatXml(newContent), _modifiedFileNames);
	}

	private void _createModel(Entity entity) throws Exception {
		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		JavaClass modelImplJavaClass = _getJavaClass(
			StringBundler.concat(
				_outputPath, "/model/impl/", entity.getName(), "Impl.java"));

		context = _putDeprecatedKeys(context, modelImplJavaClass);

		String content = _processTemplate(_tplModel, context);

		File modelFile = new File(
			StringBundler.concat(
				_serviceOutputPath, "/model/", entity.getName(), "Model.java"));

		_write(modelFile, content, _modifiedFileNames);
	}

	private void _createModelArgumentsResolver(Entity entity) throws Exception {
		File file = new File(
			StringBundler.concat(
				_outputPath, "/service/persistence/impl/", entity.getName(),
				"ModelArgumentsResolver.java"));

		if (!entity.hasPersistence() || isVersionLTE_7_3_0()) {
			if (file.exists()) {
				System.out.println("Removing " + file);

				file.delete();
			}

			return;
		}

		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		JavaClass modelImplJavaClass = _getJavaClass(
			StringBundler.concat(
				_outputPath, "/model/impl/", entity.getName(), "Impl.java"));

		context = _putDeprecatedKeys(context, modelImplJavaClass);

		String content = _processTemplate(_tplModelArgumentsResolver, context);

		_write(file, content, _modifiedFileNames);
	}

	private void _createModelCache(Entity entity) throws Exception {
		JavaClass modelImplJavaClass = _getJavaClass(
			StringBundler.concat(
				_outputPath, "/model/impl/", entity.getName(), "Impl.java"));

		Map<String, Object> context = _getContext();

		context.put("cacheFields", _getCacheFields(modelImplJavaClass));
		context.put("entity", entity);

		context = _putDeprecatedKeys(context, modelImplJavaClass);

		String content = _processTemplate(_tplModelCache, context);

		File modelFile = new File(
			StringBundler.concat(
				_outputPath, "/model/impl/", entity.getName(),
				"CacheModel.java"));

		_write(modelFile, content, _modifiedFileNames);
	}

	private void _createModelHintsXml() throws Exception {
		Map<String, Object> context = _getContext();

		context.put("entities", _entities);

		String content = _processTemplate(_tplModelHintsXml, context);

		File xmlFile = new File(_modelHintsFileName);

		if (!xmlFile.exists()) {
			_write(
				xmlFile,
				"<?xml version=\"1.0\"?>\n\n<model-hints>\n</model-hints>");
		}

		String oldContent = _read(xmlFile);

		String newContent = oldContent;

		int firstModel = newContent.indexOf(
			"<model name=\"" + _packagePath + ".model.");
		int lastModel = newContent.lastIndexOf(
			"<model name=\"" + _packagePath + ".model.");

		if (firstModel == -1) {
			firstModel = newContent.indexOf(
				"<model name=\"" + _apiPackagePath + ".model.");
			lastModel = newContent.lastIndexOf(
				"<model name=\"" + _apiPackagePath + ".model.");
		}

		if (firstModel == -1) {
			int x = newContent.indexOf("</model-hints>");

			newContent =
				newContent.substring(0, x) + content + newContent.substring(x);
		}
		else {
			firstModel = newContent.lastIndexOf("<model", firstModel) - 1;
			lastModel = newContent.indexOf("</model>", lastModel) + 9;

			newContent =
				newContent.substring(0, firstModel) + content +
					newContent.substring(lastModel);
		}

		ToolsUtil.writeFileRaw(
			xmlFile, _formatXml(newContent), _modifiedFileNames);
	}

	private void _createModelImpl(Entity entity) throws Exception {
		JavaClass modelImplJavaClass = _getJavaClass(
			StringBundler.concat(
				_outputPath, "/model/impl/", entity.getName(), "Impl.java"));

		Map<String, Object> context = _getContext();

		boolean hasClassNameCacheField = false;

		JavaField[] cacheFields = _getCacheFields(modelImplJavaClass);

		for (JavaField javaField : cacheFields) {
			if (Objects.equals(javaField.getName(), "_className")) {
				hasClassNameCacheField = true;

				break;
			}
		}

		context.put("cacheFields", _getCacheFields(modelImplJavaClass));
		context.put("entity", entity);
		context.put("hasClassNameCacheField", hasClassNameCacheField);

		context = _putDeprecatedKeys(context, modelImplJavaClass);

		String content = _processTemplate(_tplModelImpl, context);

		File modelFile = new File(
			StringBundler.concat(
				_outputPath, "/model/impl/", entity.getName(),
				"ModelImpl.java"));

		_write(modelFile, content, _modifiedFileNames);
	}

	private void _createModelSoap(Entity entity) throws Exception {
		File modelFile = new File(
			StringBundler.concat(
				_serviceOutputPath, "/model/", entity.getName(), "Soap.java"));

		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		JavaClass modelImplJavaClass = _getJavaClass(
			StringBundler.concat(
				_outputPath, "/model/impl/", entity.getName(), "Impl.java"));

		context = _putDeprecatedKeys(context, modelImplJavaClass);

		String content = _processTemplate(_tplModelSoap, context);

		_write(modelFile, content, _modifiedFileNames);
	}

	private void _createModelTable(Entity entity) throws Exception {
		String classDeprecatedComment = "";

		if (entity.isDeprecated()) {
			JavaClass modelJavaClass = _getJavaClass(
				StringBundler.concat(
					_serviceOutputPath, "/model/", entity.getName(),
					"Model.java"));

			if (modelJavaClass != null) {
				DocletTag tag = modelJavaClass.getTagByName("deprecated");

				if (tag != null) {
					classDeprecatedComment = tag.getValue();
				}
			}
		}

		_createDSLTable(
			entity.getDatabaseRegularEntityColumns(), entity.getName(),
			entity.getTable(), entity.isChangeTrackingEnabled(),
			entity.isDeprecated(), classDeprecatedComment,
			Collections.singletonList(entity.getName()));
	}

	private void _createModelWrapper(Entity entity) throws Exception {
		JavaClass modelJavaClass = _getJavaClass(
			StringBundler.concat(
				_serviceOutputPath, "/model/", entity.getName(), "Model.java"));

		List<JavaMethod> methods = _getMethods(modelJavaClass);

		JavaClass extendedModelBaseImplJavaClass = _getJavaClass(
			StringBundler.concat(
				_outputPath, "/model/impl/", entity.getName(),
				"BaseImpl.java"));

		methods = _mergeMethods(
			methods, _getMethods(extendedModelBaseImplJavaClass), false);

		JavaClass extendedModelJavaClass = _getJavaClass(
			StringBundler.concat(
				_serviceOutputPath, "/model/", entity.getName(), ".java"));

		methods = _mergeMethods(
			methods, _getMethods(extendedModelJavaClass), false);

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", methods);

		context = _putDeprecatedKeys(context, modelJavaClass);

		String content = _processTemplate(_tplModelWrapper, context);

		File modelFile = new File(
			StringBundler.concat(
				_serviceOutputPath, "/model/", entity.getName(),
				"Wrapper.java"));

		_write(modelFile, content, _modifiedFileNames);
	}

	private void _createPersistence(Entity entity) throws Exception {
		File file = new File(
			StringBundler.concat(
				_serviceOutputPath, "/service/persistence/", entity.getName(),
				"Persistence.java"));

		if (entity.hasPersistence()) {
			JavaClass javaClass = _getJavaClass(
				StringBundler.concat(
					_outputPath, "/service/persistence/impl/", entity.getName(),
					"PersistenceImpl.java"));

			Map<String, Object> context = _getContext();

			context.put("entity", entity);
			context.put("methods", _getMethods(javaClass));

			context = _putDeprecatedKeys(context, javaClass);

			String content = _processTemplate(_tplPersistence, context);

			_write(file, content, _modifiedFileNames);
		}
		else {
			if (file.exists()) {
				System.out.println("Removing " + file);

				file.delete();
			}
		}
	}

	private void _createPersistenceConstants() throws Exception {
		File file = new File(
			StringBundler.concat(
				_outputPath, "/service/persistence/impl/constants/",
				_portletShortName, "PersistenceConstants.java"));

		if (_dependencyInjectorDS) {
			String content = _processTemplate(
				_TPL_PERSISTENCE_CONSTANTS, _getContext());

			_write(file, content, _modifiedFileNames);
		}
		else if (file.exists()) {
			System.out.println("Removing " + file);

			file.delete();
		}

		File dir = file.getParentFile();

		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}

		for (File oldFile : dir.listFiles()) {
			if (!Objects.equals(file.getName(), oldFile.getName())) {
				oldFile.delete();
			}
		}
	}

	private void _createPersistenceImpl(Entity entity) throws Exception {
		File file = new File(
			StringBundler.concat(
				_outputPath, "/service/persistence/impl/", entity.getName(),
				"PersistenceImpl.java"));

		if (entity.hasPersistence()) {
			Map<String, Object> context = _getContext();

			JavaClass modelImplJavaClass = _getJavaClass(
				StringBundler.concat(
					_outputPath, "/model/impl/", entity.getName(),
					"Impl.java"));

			context.put("cacheFields", _getCacheFields(modelImplJavaClass));

			context.put("entity", entity);
			context.put("persistence", Boolean.TRUE);
			context.put("referenceEntities", _mergeReferenceEntities(entity));

			context = _putDeprecatedKeys(context, modelImplJavaClass);

			String content = _processTemplate(_tplPersistenceImpl, context);

			_write(file, content, _modifiedFileNames);
		}
		else if (file.exists()) {
			System.out.println("Removing " + file);

			file.delete();
		}

		file = new File(
			StringBundler.concat(
				_outputPath, "/service/persistence/", entity.getName(),
				"PersistenceImpl.java"));

		if (file.exists()) {
			System.out.println("Relocating " + file);

			file.delete();
		}
	}

	private void _createPersistenceTest(Entity entity) throws Exception {
		File file = new File(
			StringBundler.concat(
				_testOutputPath, "/service/persistence/test/", entity.getName(),
				"PersistenceTest.java"));

		if (entity.hasPersistence() && !entity.isDeprecated()) {
			Map<String, Object> context = _getContext();

			context.put("entity", entity);

			JavaClass modelImplJavaClass = _getJavaClass(
				StringBundler.concat(
					_outputPath, "/model/impl/", entity.getName(),
					"Impl.java"));

			context = _putDeprecatedKeys(context, modelImplJavaClass);

			String content = _processTemplate(_TPL_PERSISTENCE_TEST, context);

			_write(file, content, _modifiedFileNames);
		}
		else if (file.exists()) {
			System.out.println("Removing " + file);

			file.delete();
		}

		file = new File(
			StringBundler.concat(
				_testOutputPath, "/service/persistence/", entity.getName(),
				"PersistenceTest.java"));

		if (file.exists()) {
			System.out.println("Relocating " + file);

			file.delete();
		}
	}

	private void _createPersistenceUtil(Entity entity) throws Exception {
		File file = new File(
			StringBundler.concat(
				_serviceOutputPath, "/service/persistence/", entity.getName(),
				"Util.java"));

		if (entity.hasPersistence()) {
			JavaClass javaClass = _getJavaClass(
				StringBundler.concat(
					_outputPath, "/service/persistence/impl/", entity.getName(),
					"PersistenceImpl.java"));

			Map<String, Object> context = _getContext();

			context.put("entity", entity);
			context.put("methods", _getMethods(javaClass));

			context = _putDeprecatedKeys(context, javaClass);

			String content = _processTemplate(_tplPersistenceUtil, context);

			_write(file, content, _modifiedFileNames);
		}
		else if (file.exists()) {
			System.out.println("Removing " + file);

			file.delete();
		}
	}

	private void _createPool(Entity entity) {
		File file = new File(
			StringBundler.concat(
				_outputPath, "/service/persistence/", entity.getName(),
				"Pool.java"));

		if (file.exists()) {
			System.out.println("Removing deprecated " + file);

			file.delete();
		}
	}

	private void _createProps() throws Exception {
		if (Validator.isNull(_pluginName) && !_osgiModule) {
			return;
		}

		File propsFile = null;

		if (Validator.isNotNull(_resourcesDirName)) {
			propsFile = new File(_resourcesDirName + "/service.properties");
		}
		else {

			// Backwards compatibility

			propsFile = new File(_implDirName + "/service.properties");
		}

		long buildNumber = 1;
		long buildDate = System.currentTimeMillis();

		if (propsFile.exists()) {
			Properties properties = PropertiesUtil.load(_read(propsFile));

			if (_buildNumberIncrement && _hasModifiedSQLFiles() &&
				!_hasLocalChanges(propsFile)) {

				buildNumber =
					GetterUtil.getLong(properties.getProperty("build.number")) +
						1;
			}
			else {
				buildDate = GetterUtil.getLong(
					properties.getProperty("build.date"));
				buildNumber = GetterUtil.getLong(
					properties.getProperty("build.number"));
			}
		}

		if (!_buildNumberIncrement && (buildNumber < _buildNumber)) {
			buildNumber = _buildNumber;
			buildDate = System.currentTimeMillis();
		}

		Map<String, Object> context = _getContext();

		context.put("buildNumber", buildNumber);
		context.put("currentTimeMillis", buildDate);

		String content = _processTemplate(_tplProps, context);

		ToolsUtil.writeFileRaw(propsFile, content, _modifiedFileNames);
	}

	private void _createService(Entity entity, int sessionType)
		throws Exception {

		Set<String> imports = new HashSet<>();

		JavaClass javaClass = _getJavaClass(
			StringBundler.concat(
				_outputPath, "/service/impl/", entity.getName(),
				_getSessionTypeName(sessionType), "ServiceImpl.java"));

		JavaSource javaSource = javaClass.getSource();

		imports.addAll(javaSource.getImports());

		List<JavaMethod> methods = _getMethods(javaClass);

		JavaType superClass = javaClass.getSuperClass();

		String superClassValue = superClass.getValue();

		if (superClassValue.endsWith(
				entity.getName() + _getSessionTypeName(sessionType) +
					"ServiceBaseImpl")) {

			JavaClass parentJavaClass = _getJavaClass(
				StringBundler.concat(
					_outputPath, "/service/base/", entity.getName(),
					_getSessionTypeName(sessionType), "ServiceBaseImpl.java"));

			methods = _mergeMethods(
				methods, parentJavaClass.getMethods(), true);

			JavaSource parentJavaSource = parentJavaClass.getSource();

			Map<String, String> importsMap = new HashMap<>();

			for (String childImport : imports) {
				int x = childImport.lastIndexOf('.');

				importsMap.put(childImport.substring(x + 1), childImport);
			}

			for (String parentImport : parentJavaSource.getImports()) {
				int x = parentImport.lastIndexOf('.');

				String simpleName = parentImport.substring(x + 1);

				String conflictingImport = importsMap.get(simpleName);

				if (conflictingImport == null) {
					imports.add(parentImport);
				}
				else if (!conflictingImport.equals(parentImport)) {
					for (JavaMethod method : methods) {
						String signature = method.getDeclarationSignature(
							false);

						if (signature.contains(conflictingImport)) {
							break;
						}

						if (signature.contains(parentImport)) {
							imports.remove(conflictingImport);

							imports.add(parentImport);

							break;
						}
					}
				}
			}
		}

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("imports", imports);
		context.put("methods", methods);
		context.put("sessionTypeName", _getSessionTypeName(sessionType));

		context = _putDeprecatedKeys(context, javaClass);

		String content = _processTemplate(_tplService, context);

		File file = new File(
			StringBundler.concat(
				_serviceOutputPath, "/service/", entity.getName(),
				_getSessionTypeName(sessionType), "Service.java"));

		_write(file, content, _modifiedFileNames);
	}

	private void _createServiceBaseImpl(Entity entity, int sessionType)
		throws Exception {

		JavaClass javaClass = _getJavaClass(
			StringBundler.concat(
				_outputPath, "/service/impl/", entity.getName(),
				(sessionType != _SESSION_TYPE_REMOTE) ? "Local" : "",
				"ServiceImpl.java"));

		List<JavaMethod> methods = _getMethods(javaClass);

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", methods);
		context.put("referenceEntities", _mergeReferenceEntities(entity));
		context.put("sessionTypeName", _getSessionTypeName(sessionType));

		context = _putDeprecatedKeys(context, javaClass);

		String content = _processTemplate(_tplServiceBaseImpl, context);

		File file = new File(
			StringBundler.concat(
				_outputPath, "/service/base/", entity.getName(),
				_getSessionTypeName(sessionType), "ServiceBaseImpl.java"));

		_write(file, content, _modifiedFileNames);
	}

	private void _createServiceFactory(Entity entity, int sessionType) {
		File file = new File(
			StringBundler.concat(
				_oldServiceOutputPath, "/service/", entity.getName(),
				_getSessionTypeName(sessionType), "ServiceFactory.java"));

		if (file.exists()) {
			System.out.println("Removing deprecated " + file);

			file.delete();
		}

		file = new File(
			StringBundler.concat(
				_outputPath, "/service/", entity.getName(),
				_getSessionTypeName(sessionType), "ServiceFactory.java"));

		if (file.exists()) {
			System.out.println("Removing deprecated " + file);

			file.delete();
		}
	}

	private void _createServiceHttp(Entity entity) throws Exception {
		JavaClass javaClass = _getJavaClass(
			StringBundler.concat(
				_outputPath, "/service/impl/", entity.getName(),
				"ServiceImpl.java"));

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("hasHttpMethods", _hasHttpMethods(javaClass));
		context.put("methods", _getMethods(javaClass));

		context = _putDeprecatedKeys(context, javaClass);

		String content = _processTemplate(_tplServiceHttp, context);

		File file = new File(
			StringBundler.concat(
				_outputPath, "/service/http/", entity.getName(),
				"ServiceHttp.java"));

		_write(file, content, _modifiedFileNames);
	}

	private void _createServiceImpl(Entity entity, int sessionType)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_outputPath, "/service/impl/", entity.getName(),
				_getSessionTypeName(sessionType), "ServiceImpl.java"));

		if (file.exists()) {
			return;
		}

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("sessionTypeName", _getSessionTypeName(sessionType));

		String content = _processTemplate(_tplServiceImpl, context);

		_write(file, content, _modifiedFileNames);
	}

	private void _createServicePropsUtil() throws Exception {
		if (!_osgiModule) {
			return;
		}

		File file = new File(
			StringBundler.concat(
				_implDirName, "/", StringUtil.replace(_propsUtil, '.', '/'),
				".java"));

		if (_dependencyInjectorDS) {
			if (file.exists()) {
				file.delete();

				System.out.println("Removing " + file);
			}

			return;
		}

		Map<String, Object> context = _getContext();

		int index = _propsUtil.lastIndexOf(".");

		context.put(
			"servicePropsUtilClassName", _propsUtil.substring(index + 1));
		context.put(
			"servicePropsUtilPackagePath", _propsUtil.substring(0, index));

		String content = _processTemplate(_tplServicePropsUtil, context);

		_write(file, content, _modifiedFileNames);
	}

	private void _createServiceSoap(Entity entity) throws Exception {
		JavaClass javaClass = _getJavaClass(
			StringBundler.concat(
				_outputPath, "/service/impl/", entity.getName(),
				"ServiceImpl.java"));

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", _getMethods(javaClass));

		context = _putDeprecatedKeys(context, javaClass);

		String content = _processTemplate(_tplServiceSoap, context);

		File file = new File(
			StringBundler.concat(
				_outputPath, "/service/http/", entity.getName(),
				"ServiceSoap.java"));

		_write(file, content, _modifiedFileNames);
	}

	private void _createServiceUtil(Entity entity, int sessionType)
		throws Exception {

		JavaClass javaClass = _getJavaClass(
			StringBundler.concat(
				_serviceOutputPath, "/service/", entity.getName(),
				_getSessionTypeName(sessionType), "Service.java"));

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", _getMethods(javaClass));
		context.put("sessionTypeName", _getSessionTypeName(sessionType));

		context = _putDeprecatedKeys(context, javaClass);

		String content = _processTemplate(_tplServiceUtil, context);

		File file = new File(
			StringBundler.concat(
				_serviceOutputPath, "/service/", entity.getName(),
				_getSessionTypeName(sessionType), "ServiceUtil.java"));

		_write(file, content, _modifiedFileNames);
	}

	private void _createServiceWrapper(Entity entity, int sessionType)
		throws Exception {

		JavaClass javaClass = _getJavaClass(
			StringBundler.concat(
				_serviceOutputPath, "/service/", entity.getName(),
				_getSessionTypeName(sessionType), "Service.java"));

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", _getMethods(javaClass));
		context.put("sessionTypeName", _getSessionTypeName(sessionType));

		context = _putDeprecatedKeys(context, javaClass);

		String content = _processTemplate(_tplServiceWrapper, context);

		File file = new File(
			StringBundler.concat(
				_serviceOutputPath, "/service/", entity.getName(),
				_getSessionTypeName(sessionType), "ServiceWrapper.java"));

		_write(file, content, _modifiedFileNames);
	}

	private void _createServletContextUtil() throws Exception {
		if (Validator.isNull(_pluginName)) {
			return;
		}

		String content = _processTemplate(
			_TPL_SERVLET_CONTEXT_UTIL, _getContext());

		File file = new File(
			_serviceOutputPath + "/service/ServletContextUtil.java");

		_write(file, content, _modifiedFileNames);
	}

	private void _createSpringXml() throws Exception {
		if (_packagePath.equals("com.liferay.counter")) {
			return;
		}

		File xmlFile = new File(_springFileName);

		if (_dependencyInjectorDS) {
			if (xmlFile.exists()) {
				xmlFile.delete();

				System.out.println("Removing " + xmlFile);
			}

			return;
		}

		Map<String, Object> context = _getContext();

		context.put("entities", _entities);

		String content = _processTemplate(_tplSpringXml, context);

		StringBundler sb = new StringBundler(11);

		sb.append("<?xml version=\"1.0\"?>\n\n");
		sb.append("<beans\n");
		sb.append("\tdefault-destroy-method=\"destroy\"\n");
		sb.append("\tdefault-init-method=\"afterPropertiesSet\"\n");
		sb.append(_getSpringNamespacesDeclarations());
		sb.append("\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		sb.append("\n");
		sb.append("\txsi:schemaLocation=\"");
		sb.append(StringUtil.trim(_getSpringSchemaLocations()));
		sb.append("\">\n");
		sb.append("</beans>");

		String xml = sb.toString();

		if (!xmlFile.exists()) {
			_write(xmlFile, xml);
		}

		String oldContent = _read(xmlFile);

		if (Validator.isNotNull(_pluginName) &&
			oldContent.contains("DOCTYPE beans PUBLIC")) {

			oldContent = xml;
		}

		String newContent = _fixSpringXml(oldContent);

		int x = oldContent.indexOf("<beans");
		int y = oldContent.lastIndexOf("</beans>");

		int firstSession = newContent.indexOf(
			"<bean class=\"" + _packagePath + ".service.", x);

		int lastSession = newContent.lastIndexOf(
			"<bean class=\"" + _packagePath + ".service.", y);

		if (firstSession == -1) {
			firstSession = newContent.indexOf(
				"<bean class=\"" + _apiPackagePath + ".service.", x);

			lastSession = newContent.lastIndexOf(
				"<bean class=\"" + _apiPackagePath + ".service.", y);
		}

		if ((firstSession == -1) || (firstSession > y)) {
			x = newContent.indexOf("</beans>");

			newContent =
				newContent.substring(0, x) + content + newContent.substring(x);
		}
		else {
			firstSession = newContent.lastIndexOf("<bean", firstSession) - 1;

			int tempLastSession = newContent.indexOf(
				"<bean class=\"", lastSession + 1);

			if (tempLastSession == -1) {
				tempLastSession = newContent.indexOf("</beans>", lastSession);
			}

			lastSession = tempLastSession;

			newContent =
				newContent.substring(0, firstSession) + content +
					newContent.substring(lastSession);
		}

		newContent = _formatXml(newContent);

		Matcher matcher = _beansPattern.matcher(newContent);

		if (matcher.find()) {
			String beans = matcher.group();
			Map<String, String> beansAttributes = new TreeMap<>(
				String.CASE_INSENSITIVE_ORDER);

			matcher = _beansAttributePattern.matcher(beans);

			while (matcher.find()) {
				String beanAttribute = StringBundler.concat(
					StringUtil.trim(matcher.group(1)), "=\"",
					StringUtil.trim(matcher.group(2)), "\"");

				beansAttributes.put(
					StringUtil.trim(matcher.group(1)), beanAttribute);
			}

			sb.setIndex(0);

			for (Map.Entry<String, String> beanAttribute :
					beansAttributes.entrySet()) {

				sb.append("\n\t");
				sb.append(beanAttribute.getValue());
			}

			newContent = StringUtil.replaceFirst(
				newContent, beans, "<beans" + sb.toString() + "\n>");
		}

		ToolsUtil.writeFileRaw(xmlFile, newContent, _modifiedFileNames);
	}

	private void _createSQLIndexes() throws Exception {
		File sqlDir = new File(_sqlDirName);

		if (!sqlDir.exists()) {
			_mkdir(sqlDir);
		}

		// indexes.sql loading

		File sqlFile = new File(_sqlDirName + "/" + _sqlIndexesFileName);

		if (!sqlFile.exists()) {
			_touch(sqlFile);
		}

		Map<String, List<IndexMetadata>> indexMetadatasMap = new TreeMap<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new FileReader(sqlFile))) {

			iterate:
			while (true) {
				String indexSQL = unsyncBufferedReader.readLine();

				if (indexSQL == null) {
					break;
				}

				indexSQL = indexSQL.trim();

				if (Validator.isNull(indexSQL)) {
					continue;
				}

				IndexMetadata indexMetadata =
					IndexMetadataFactoryUtil.createIndexMetadata(indexSQL);

				List<String> pkEntityColumnDBNames = null;

				Entity entity = _getEntityByTableName(
					indexMetadata.getTableName());

				if (entity != null) {
					indexMetadata = new IndexMetadata(
						indexMetadata.getIndexName(),
						indexMetadata.getTableName(), indexMetadata.isUnique(),
						indexMetadata.getColumnNames());

					for (String columnName : indexMetadata.getColumnNames()) {
						EntityColumn entityColumn =
							_fetchEntityColumnByColumnDBName(
								entity, columnName);

						if (entityColumn == null) {
							System.out.println(
								StringBundler.concat(
									"Removing index ",
									indexMetadata.getIndexName(),
									" because column \"", columnName,
									"\" does not exist"));

							continue iterate;
						}
					}

					pkEntityColumnDBNames = entity.getPKEntityColumnDBNames();
				}
				else {
					EntityMapping entityMapping = _entityMappings.get(
						indexMetadata.getTableName());

					pkEntityColumnDBNames =
						_getEntityMappingPKEntityColumnDBNames(entityMapping);
				}

				_addIndexMetadata(
					indexMetadatasMap, indexMetadata.getTableName(),
					pkEntityColumnDBNames, indexMetadata);
			}
		}

		// indexes.sql appending

		for (Entity entity : _entities) {
			if (!_isTargetEntity(entity) || !entity.isDefaultDataSource() ||
				entity.isDeprecated()) {

				continue;
			}

			if (!entity.hasFinderClassName() && !entity.hasPersistence()) {
				continue;
			}

			String tableName = entity.getTable();

			List<IndexMetadata> indexMetadatas = indexMetadatasMap.get(
				tableName);

			if ((indexMetadatas != null) && _optimizeDBIndexes) {
				indexMetadatas.clear();
			}

			List<EntityFinder> entityFinders = entity.getEntityFinders();

			for (EntityFinder entityFinder : entityFinders) {
				if (!entityFinder.isDBIndex()) {
					continue;
				}

				List<EntityColumn> entityColumns =
					entityFinder.getEntityColumns();

				if (entityColumns.equals(entity.getPKEntityColumns())) {
					continue;
				}

				List<String> dbNames = new ArrayList<>();

				for (EntityColumn entityColumn : entityColumns) {
					if (entityColumn.isIndexable()) {
						dbNames.add(entityColumn.getDBName());
					}
				}

				if (dbNames.isEmpty()) {
					continue;
				}

				boolean unique = entityFinder.isUnique();

				if (unique && entity.isChangeTrackingEnabled() &&
					!dbNames.contains("ctCollectionId")) {

					dbNames.add("ctCollectionId");
				}

				if (_optimizeDBIndexes && !unique) {
					for (String highCardinalityColumnName :
							_highCardinalityColumnNames) {

						if (dbNames.contains(highCardinalityColumnName) &&
							(dbNames.size() > 1)) {

							dbNames.clear();

							dbNames.add(highCardinalityColumnName);

							break;
						}
					}
				}

				IndexMetadata indexMetadata =
					IndexMetadataFactoryUtil.createIndexMetadata(
						unique, tableName, dbNames.toArray(new String[0]));

				_addIndexMetadata(
					indexMetadatasMap, indexMetadata.getTableName(),
					entity.getPKEntityColumnDBNames(), indexMetadata);
			}

			indexMetadatas = indexMetadatasMap.get(tableName);

			if (_optimizeDBIndexes && (indexMetadatas != null)) {
				indexMetadatasMap.put(
					tableName, _optimizeForBTreeIndexes(indexMetadatas));
			}
		}

		for (Map.Entry<String, EntityMapping> entry :
				_entityMappings.entrySet()) {

			EntityMapping entityMapping = entry.getValue();

			indexMetadatasMap.remove(entityMapping.getTableName());

			_getCreateMappingTableIndex(entityMapping, indexMetadatasMap);
		}

		StringBundler sb = new StringBundler();

		for (List<IndexMetadata> indexMetadatas : indexMetadatasMap.values()) {
			Collections.sort(indexMetadatas);

			for (IndexMetadata indexMetadata : indexMetadatas) {
				sb.append(
					indexMetadata.getCreateSQL(
						_getColumnLengths(indexMetadata)));

				sb.append(StringPool.NEW_LINE);
			}

			sb.append(StringPool.NEW_LINE);
		}

		if (!indexMetadatasMap.isEmpty()) {
			sb.setIndex(sb.index() - 2);
		}

		ToolsUtil.writeFileRaw(sqlFile, sb.toString(), _modifiedFileNames);

		// indexes.properties

		File file = new File(_sqlDirName, "indexes.properties");

		file.delete();
	}

	private void _createSQLMappingTables(
			File sqlFile, String newCreateTableString,
			EntityMapping entityMapping, boolean addMissingTables)
		throws Exception {

		if (!sqlFile.exists()) {
			_touch(sqlFile);
		}

		String content = _read(sqlFile);

		int x = content.indexOf(
			_SQL_CREATE_TABLE + entityMapping.getTableName() + " (");

		int y = content.indexOf(");", x);

		if (x != -1) {
			String oldCreateTableString = content.substring(x + 1, y);

			if (!oldCreateTableString.equals(newCreateTableString)) {
				content =
					content.substring(0, x) + newCreateTableString +
						content.substring(y + 2);

				ToolsUtil.writeFileRaw(sqlFile, content, _modifiedFileNames);
			}
		}
		else if (addMissingTables) {
			try (UnsyncBufferedReader unsyncBufferedReader =
					new UnsyncBufferedReader(new UnsyncStringReader(content))) {

				StringBundler sb = new StringBundler();

				String line = null;
				boolean appendNewTable = true;

				while ((line = unsyncBufferedReader.readLine()) != null) {
					if (appendNewTable && line.startsWith(_SQL_CREATE_TABLE)) {
						x = _SQL_CREATE_TABLE.length();

						y = line.indexOf(" ", x);

						String tableName = line.substring(x, y);

						if (tableName.compareTo(entityMapping.getTableName()) >
								0) {

							sb.append(newCreateTableString);
							sb.append("\n\n");

							appendNewTable = false;
						}
					}

					sb.append(line);
					sb.append("\n");
				}

				if (appendNewTable) {
					sb.append("\n");
					sb.append(newCreateTableString);
				}

				ToolsUtil.writeFileRaw(
					sqlFile, sb.toString(), _modifiedFileNames);
			}
		}
	}

	private void _createSQLSequences() throws IOException {
		File sqlDir = new File(_sqlDirName);

		if (!sqlDir.exists()) {
			_mkdir(sqlDir);
		}

		File sqlFile = new File(_sqlDirName + "/" + _sqlSequencesFileName);

		if (!sqlFile.exists()) {
			_touch(sqlFile);
		}

		Set<String> sequenceSQLs = new TreeSet<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new FileReader(sqlFile))) {

			while (true) {
				String sequenceSQL = unsyncBufferedReader.readLine();

				if (sequenceSQL == null) {
					break;
				}

				if (Validator.isNotNull(sequenceSQL)) {
					sequenceSQLs.add(sequenceSQL);
				}
			}
		}

		for (Entity entity : _entities) {
			if (!_isTargetEntity(entity) || !entity.isDefaultDataSource()) {
				continue;
			}

			if (!entity.hasFinderClassName() && !entity.hasPersistence()) {
				continue;
			}

			List<EntityColumn> entityColumns = entity.getEntityColumns();

			for (EntityColumn entityColumn : entityColumns) {
				if (Objects.equals(entityColumn.getIdType(), "sequence")) {
					StringBundler sb = new StringBundler(3);

					String sequenceName = entityColumn.getIdParam();

					if (sequenceName.length() > 30) {
						sequenceName = sequenceName.substring(0, 30);
					}

					sb.append("create sequence ");
					sb.append(sequenceName);
					sb.append(";");

					String sequenceSQL = sb.toString();

					if (!sequenceSQLs.contains(sequenceSQL)) {
						sequenceSQLs.add(sequenceSQL);
					}
				}
			}
		}

		StringBundler sb = new StringBundler(sequenceSQLs.size() * 2);

		for (String sequenceSQL : sequenceSQLs) {
			sb.append(sequenceSQL);
			sb.append("\n");
		}

		if (!sequenceSQLs.isEmpty()) {
			sb.setIndex(sb.index() - 1);
		}

		ToolsUtil.writeFileRaw(sqlFile, sb.toString(), _modifiedFileNames);
	}

	private void _createSQLTables() throws Exception {
		File sqlDir = new File(_sqlDirName);

		if (!sqlDir.exists()) {
			_mkdir(sqlDir);
		}

		File sqlFile = new File(_sqlDirName + "/" + _sqlFileName);

		if (!sqlFile.exists()) {
			_touch(sqlFile);
		}

		for (Entity entity : _entities) {
			if (!_isTargetEntity(entity) || !entity.isDefaultDataSource() ||
				entity.isDeprecated()) {

				continue;
			}

			if (!entity.hasFinderClassName() && !entity.hasPersistence()) {
				continue;
			}

			String createTableSQL = _getCreateTableSQL(entity);

			if (Validator.isNotNull(createTableSQL)) {
				_createSQLTables(sqlFile, createTableSQL, entity, true);

				if (GetterUtil.getBoolean(
						_compatProperties.getProperty(
							"update.sql.file.auto.update"))) {

					List<Path> updateSQLFilePaths = _getUpdateSQLFilePaths();

					for (Path updateSQLFilePath : updateSQLFilePaths) {
						if ((updateSQLFilePath != null) &&
							Files.exists(updateSQLFilePath)) {

							_createSQLTables(
								updateSQLFilePath.toFile(), createTableSQL,
								entity, false);
						}
					}
				}
			}
		}

		for (Map.Entry<String, EntityMapping> entry :
				_entityMappings.entrySet()) {

			EntityMapping entityMapping = entry.getValue();

			String createMappingTableSQL = _getCreateMappingTableSQL(
				entityMapping);

			if (Validator.isNotNull(createMappingTableSQL)) {
				_createSQLMappingTables(
					sqlFile, createMappingTableSQL, entityMapping, true);
			}
		}

		String content = _read(sqlFile);

		ToolsUtil.writeFileRaw(sqlFile, content.trim(), _modifiedFileNames);
	}

	private void _createSQLTables(
			File sqlFile, String newCreateTableString, Entity entity,
			boolean addMissingTables)
		throws IOException {

		if (!sqlFile.exists()) {
			_touch(sqlFile);
		}

		String content = _read(sqlFile);

		int x = content.indexOf(_SQL_CREATE_TABLE + entity.getTable() + " (");

		int y = content.indexOf(");", x);

		if (x != -1) {
			String oldCreateTableString = content.substring(x, y + 2);

			if (!oldCreateTableString.equals(newCreateTableString)) {
				content =
					content.substring(0, x) + newCreateTableString +
						content.substring(y + 2);

				ToolsUtil.writeFileRaw(sqlFile, content, _modifiedFileNames);
			}
		}
		else if (addMissingTables) {
			try (UnsyncBufferedReader unsyncBufferedReader =
					new UnsyncBufferedReader(new UnsyncStringReader(content))) {

				StringBundler sb = new StringBundler();

				String line = null;
				boolean appendNewTable = true;

				while ((line = unsyncBufferedReader.readLine()) != null) {
					if (appendNewTable && line.startsWith(_SQL_CREATE_TABLE)) {
						x = _SQL_CREATE_TABLE.length();

						y = line.indexOf(" ", x);

						String tableName = line.substring(x, y);

						if (tableName.compareTo(entity.getTable()) > 0) {
							sb.append(newCreateTableString);
							sb.append("\n\n");

							appendNewTable = false;
						}
					}

					sb.append(line);
					sb.append("\n");
				}

				if (appendNewTable) {
					sb.append("\n");
					sb.append(newCreateTableString);
				}

				ToolsUtil.writeFileRaw(
					sqlFile, sb.toString(), _modifiedFileNames);
			}
		}
	}

	private void _createUADAnonymizer(Entity entity) throws Exception {
		File file = new File(
			StringBundler.concat(
				entity.getUADOutputPath(), "/uad/anonymizer/", entity.getName(),
				"UADAnonymizer.java"));

		if (file.exists()) {
			return;
		}

		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		String content = _processTemplate(_TPL_UAD_ANOYMIZER, context);

		_write(file, content, _modifiedFileNames);
	}

	private void _createUADBnd(String uadApplicationName) throws Exception {
		List<Entity> entities = _uadApplicationEntities.get(uadApplicationName);

		Entity entity = entities.get(0);

		String uadOutputPath = entity.getUADOutputPath();

		int index = uadOutputPath.indexOf("/src/");

		String uadDirName = uadOutputPath.substring(0, index);

		File file = new File(uadDirName + "/bnd.bnd");

		if (file.exists()) {
			return;
		}

		Map<String, Object> context = _getContext();

		context.put(
			"uadBundleName",
			StringBundler.concat(
				"Liferay ",
				TextFormatter.format(
					TextFormatter.format(uadApplicationName, TextFormatter.K),
					TextFormatter.J),
				" UAD"));
		context.put("uadPackagePath", entity.getUADPackagePath());

		String content = _processTemplate(_TPL_UAD_BND, context);

		ToolsUtil.writeFileRaw(file, content, _modifiedFileNames);
	}

	private void _createUADConstants(String uadApplicationName)
		throws Exception {

		Map<String, Object> context = _getContext();

		List<Entity> entities = _uadApplicationEntities.get(uadApplicationName);

		context.put("entities", entities);

		Entity entity = entities.get(0);

		context.put("uadApplicationName", uadApplicationName);
		context.put("uadPackagePath", entity.getUADPackagePath());

		String content = _processTemplate(_TPL_UAD_CONSTANTS, context);

		File file = new File(
			StringBundler.concat(
				entity.getUADOutputPath(), "/uad/constants/",
				uadApplicationName, "UADConstants.java"));

		_write(file, content, _modifiedFileNames);
	}

	private void _createUADDisplay(Entity entity) throws Exception {
		File file = new File(
			StringBundler.concat(
				entity.getUADOutputPath(), "/uad/display/", entity.getName(),
				"UADDisplay.java"));

		if (file.exists()) {
			return;
		}

		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		String content = _processTemplate(_TPL_UAD_DISPLAY, context);

		_write(file, content, _modifiedFileNames);
	}

	private void _createUADExporter(Entity entity) throws Exception {
		File file = new File(
			StringBundler.concat(
				entity.getUADOutputPath(), "/uad/exporter/", entity.getName(),
				"UADExporter.java"));

		if (file.exists()) {
			return;
		}

		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		String content = _processTemplate(_TPL_UAD_EXPORTER, context);

		_write(file, content, _modifiedFileNames);
	}

	private void _deleteFile(String fileName) {
		File file = new File(fileName);

		file.delete();
	}

	private void _deleteOrmXml() throws Exception {
		if (Validator.isNull(_pluginName)) {
			return;
		}

		_deleteFile("docroot/WEB-INF/src/META-INF/portlet-orm.xml");
	}

	private void _deleteSpringLegacyXml() throws Exception {
		if (Validator.isNull(_pluginName)) {
			return;
		}

		_deleteFile("docroot/WEB-INF/src/META-INF/base-spring.xml");
		_deleteFile("docroot/WEB-INF/src/META-INF/cluster-spring.xml");
		_deleteFile("docroot/WEB-INF/src/META-INF/data-source-spring.xml");
		_deleteFile(
			"docroot/WEB-INF/src/META-INF/dynamic-data-source-spring.xml");
		_deleteFile("docroot/WEB-INF/src/META-INF/hibernate-spring.xml");
		_deleteFile("docroot/WEB-INF/src/META-INF/infrastructure-spring.xml");
		_deleteFile("docroot/WEB-INF/src/META-INF/misc-spring.xml");
	}

	private EntityColumn _fetchEntityColumnByColumnDBName(
		Entity entity, String columnDBName) {

		for (EntityColumn entityColumn : entity.getFinderEntityColumns()) {
			if (columnDBName.equals(entityColumn.getDBName())) {
				return entityColumn;
			}
		}

		for (EntityColumn entityColumn : entity.getEntityColumns()) {
			if (columnDBName.equals(entityColumn.getDBName())) {
				return entityColumn;
			}
		}

		return null;
	}

	private String _fixHbmXml(String content) throws Exception {
		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			StringBundler sb = new StringBundler();

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				if (isVersionGTE_7_4_0() &&
					line.startsWith("<!DOCTYPE hibernate-mapping") &&
					line.contains(_HIBERNATE_3_HBM_NAMESPACE)) {

					line = StringUtil.replace(
						line, _HIBERNATE_3_HBM_NAMESPACE,
						_HIBERNATE_5_HBM_NAMESPACE);
				}
				else if (line.startsWith("\t<class name=\"")) {
					line = StringUtil.replace(
						line,
						new String[] {
							".service.persistence.", "HBM\" table=\""
						},
						new String[] {".model.", "\" table=\""});

					if (!line.contains(".model.impl.") &&
						!line.contains("BlobModel")) {

						line = StringUtil.replace(
							line, new String[] {".model.", "\" table=\""},
							new String[] {".model.impl.", "Impl\" table=\""});
					}
				}

				sb.append(line);
				sb.append('\n');
			}

			return StringUtil.trim(sb.toString());
		}
	}

	private String _fixSpringXml(String content) {
		return StringUtil.replace(content, ".service.spring.", ".service.");
	}

	private String _formatComment(
		String comment, List<DocletTag> tags, String indentation) {

		StringBundler sb = new StringBundler();

		if (Validator.isNull(comment) && tags.isEmpty()) {
			return sb.toString();
		}

		sb.append(indentation);
		sb.append("/**\n");

		if (Validator.isNotNull(comment)) {
			comment = comment.replaceAll("(?m)^", indentation + " * ");

			sb.append(comment);

			sb.append("\n");

			if (!tags.isEmpty()) {
				sb.append(indentation);
				sb.append(" *\n");
			}
		}

		for (DocletTag tag : tags) {
			String tagValue = tag.getValue();

			sb.append(indentation);
			sb.append(" * @");
			sb.append(tag.getName());
			sb.append(" ");

			if (_currentTplName.equals(_tplServiceSoap)) {
				if (tagValue.startsWith(PortalException.class.getName())) {
					tagValue = tagValue.replaceFirst(
						PortalException.class.getName(), "RemoteException");
				}
				else if (tagValue.startsWith(
							PrincipalException.class.getName())) {

					tagValue = tagValue.replaceFirst(
						PrincipalException.class.getName(), "RemoteException");
				}
			}

			sb.append(tagValue);

			sb.append("\n");
		}

		sb.append(indentation);
		sb.append(" */\n");

		return sb.toString();
	}

	private String _formatXml(String xml) throws Exception {
		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		OutputFormat outputFormat = new OutputFormat(StringPool.TAB, true);

		outputFormat.setOmitEncoding(true);
		outputFormat.setPadText(true);
		outputFormat.setTrimText(true);

		XMLWriter xmlWriter = new XMLWriter(
			unsyncByteArrayOutputStream, outputFormat);

		SAXReader saxReader = _getSAXReader();

		String doctype = null;

		int x = xml.indexOf("<!DOCTYPE");

		if (x != -1) {
			int y = xml.indexOf(">", x) + 1;

			doctype = xml.substring(x, y);

			xml = xml.substring(0, x) + "\n" + xml.substring(y);
		}

		xml = StringUtil.replace(xml, '\r', "");

		xmlWriter.write(saxReader.read(new XMLSafeReader(xml)));

		xml = StringUtil.trimTrailing(
			unsyncByteArrayOutputStream.toString(StringPool.UTF8));

		while (xml.contains(" \n")) {
			xml = StringUtil.replace(xml, " \n", "\n");
		}

		xml = StringUtil.replace(xml, "\"/>", "\" />");

		if (Validator.isNotNull(doctype)) {
			x = xml.indexOf("?>") + 2;

			xml = StringBundler.concat(
				xml.substring(0, x), "\n", doctype, xml.substring(x));
		}

		return xml;
	}

	private List<EntityColumn> _getBlobEntityColumns(Entity entity) {
		List<EntityColumn> blobEntityColumns = new ArrayList<>(
			entity.getBlobEntityColumns());

		Iterator<EntityColumn> iterator = blobEntityColumns.iterator();

		while (iterator.hasNext()) {
			EntityColumn entityColumn = iterator.next();

			if (!entityColumn.isLazy()) {
				iterator.remove();
			}
		}

		return blobEntityColumns;
	}

	private JavaField[] _getCacheFields(JavaClass javaClass) {
		if (javaClass == null) {
			return new JavaField[0];
		}

		List<JavaField> javaFields = new ArrayList<>();

		for (JavaField javaField : javaClass.getFields()) {
			List<JavaAnnotation> javaAnnotations = javaField.getAnnotations();

			for (JavaAnnotation javaAnnotation : javaAnnotations) {
				JavaClass javaAnnotationClass = javaAnnotation.getType();

				String className = javaAnnotationClass.getFullyQualifiedName();

				if (className.equals(CacheField.class.getName())) {
					javaFields.add(javaField);

					break;
				}
			}
		}

		return javaFields.toArray(new JavaField[0]);
	}

	private int[] _getColumnLengths(IndexMetadata indexMetadata) {
		Entity entity = _getEntityByTableName(indexMetadata.getTableName());

		if (entity == null) {
			return null;
		}

		String[] columnNames = indexMetadata.getColumnNames();

		int[] columnLengths = new int[columnNames.length];

		for (int i = 0; i < columnNames.length; i++) {
			EntityColumn entityColumn = _getEntityColumnByColumnDBName(
				entity, columnNames[i]);

			String colType = entityColumn.getType();

			if (colType.equals("String")) {
				columnLengths[i] = getMaxLength(entity.getName(), entityColumn);
			}
		}

		return columnLengths;
	}

	private Properties _getCompatProperties(String version) throws IOException {
		Properties properties = new Properties();

		try (InputStream inputStream = ServiceBuilder.class.getResourceAsStream(
				"dependencies/" + version + "/compatibility.properties")) {

			properties.load(inputStream);
		}

		return properties;
	}

	private Configuration _getConfiguration() {
		if (_configuration != null) {
			return _configuration;
		}

		_configuration = new Configuration(Configuration.VERSION_2_3_33);

		_configuration.setNumberFormat("computer");

		DefaultObjectWrapperBuilder defaultObjectWrapperBuilder =
			new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_33);

		_configuration.setObjectWrapper(defaultObjectWrapperBuilder.build());

		_configuration.setTemplateLoader(
			new ClassTemplateLoader(ServiceBuilder.class, StringPool.SLASH));
		_configuration.setTemplateUpdateDelayMilliseconds(Long.MAX_VALUE);

		return _configuration;
	}

	private Map<String, Object> _getContext() throws Exception {
		Map<String, Object> context = HashMapBuilder.<String, Object>put(
			"apiPackagePath", _apiPackagePath
		).put(
			"author", _author
		).put(
			"beanLocatorUtil", _beanLocatorUtil
		).put(
			"dependencyInjectorDS", _dependencyInjectorDS
		).put(
			"modelHintsUtil", ModelHintsUtil.getModelHints()
		).put(
			"osgiModule", _osgiModule
		).put(
			"packagePath", _packagePath
		).put(
			"pluginName", _pluginName
		).put(
			"portletShortName", _portletShortName
		).put(
			"propsUtil", _propsUtil
		).put(
			"serviceBuilder", this
		).put(
			"stringUtil", StringUtil_IW.getInstance()
		).put(
			"textFormatter",
			() -> {
				BeansWrapper beansWrapper = BeansWrapper.getDefaultInstance();

				TemplateHashModel staticModels = beansWrapper.getStaticModels();

				return staticModels.get(TextFormatter.class.getName());
			}
		).put(
			"validator", Validator_IW.getInstance()
		).build();

		//context.put("system", staticModels.get("java.lang.System"));

		return context;
	}

	private void _getCreateMappingTableIndex(
			EntityMapping entityMapping,
			Map<String, List<IndexMetadata>> indexMetadatasMap)
		throws Exception {

		Entity[] entities = new Entity[3];

		for (int i = 0; i < entities.length; i++) {
			entities[i] = getEntity(entityMapping.getEntityName(i));

			if (entities[i] == null) {
				return;
			}
		}

		List<String> mappingPKEntityColumnDBNames =
			_getEntityMappingPKEntityColumnDBNames(entityMapping);

		String tableName = entityMapping.getTableName();

		for (Entity entity : entities) {
			for (String dbName : entity.getPKEntityColumnDBNames()) {
				IndexMetadata indexMetadata =
					IndexMetadataFactoryUtil.createIndexMetadata(
						false, tableName, dbName);

				_addIndexMetadata(
					indexMetadatasMap, tableName, mappingPKEntityColumnDBNames,
					indexMetadata);
			}
		}
	}

	private String _getCreateMappingTableSQL(EntityMapping entityMapping)
		throws Exception {

		Entity[] entities = new Entity[3];

		for (int i = 0; i < entities.length; i++) {
			entities[i] = getEntity(entityMapping.getEntityName(i));

			if (entities[i] == null) {
				return null;
			}
		}

		Arrays.sort(
			entities,
			new Comparator<Entity>() {

				@Override
				public int compare(Entity entity1, Entity entity2) {
					String name1 = entity1.getName();

					if (Objects.equals(
							entity1.getPackagePath(), "com.liferay.portal") &&
						name1.equals("Company")) {

						return -1;
					}

					String name2 = entity2.getName();

					if (Objects.equals(
							entity2.getPackagePath(), "com.liferay.portal") &&
						name2.equals("Company")) {

						return 1;
					}

					return name1.compareTo(name2);
				}

			});

		StringBundler sb = new StringBundler();

		sb.append(_SQL_CREATE_TABLE);

		String tableName = entityMapping.getTableName();

		if ((_databaseNameMaxLength > 0) &&
			(tableName.length() > _databaseNameMaxLength)) {

			throw new ServiceBuilderException(
				StringBundler.concat(
					"Unable to create entity mapping \"", tableName,
					"\" because table name exceeds ", _databaseNameMaxLength,
					" characters. Some databases do not allow table names ",
					"longer than 30 characters. To disable this warning set ",
					"the \"service-builder\" attribute \"database-name-max-",
					"length\" to the max length that your database supports."));
		}

		sb.append(tableName);

		sb.append(" (\n");

		for (Entity entity : entities) {
			List<EntityColumn> pkEntityColumns = entity.getPKEntityColumns();

			for (EntityColumn entityColumn : pkEntityColumns) {
				String dbName = entityColumn.getDBName();

				if ((_databaseNameMaxLength > 0) &&
					(dbName.length() > _databaseNameMaxLength)) {

					throw new ServiceBuilderException(
						StringBundler.concat(
							"Unable to create entity mapping \"", tableName,
							"\" because column name \"", dbName, "\" exceeds ",
							_databaseNameMaxLength,
							" characters. Some databases do not allow column ",
							"names longer than 30 characters. To disable this ",
							"warning set the \"service-builder\" attribute ",
							"\"database-name-max-length\" to the max length ",
							"that your database supports."));
				}

				String type = entityColumn.getType();

				sb.append("\t");
				sb.append(dbName);
				sb.append(" ");

				if (StringUtil.equalsIgnoreCase(type, "boolean")) {
					sb.append("BOOLEAN");
				}
				else if (StringUtil.equalsIgnoreCase(type, "double") ||
						 StringUtil.equalsIgnoreCase(type, "float")) {

					sb.append("DOUBLE");
				}
				else if (type.equals("int") || type.equals("Integer") ||
						 StringUtil.equalsIgnoreCase(type, "short")) {

					sb.append("INTEGER");
				}
				else if (StringUtil.equalsIgnoreCase(type, "long")) {
					sb.append("LONG");
				}
				else if (type.equals("Map")) {
					sb.append("TEXT");
				}
				else if (type.equals("String")) {
					int maxLength = getMaxLength(
						entity.getName(), entityColumn);

					if (entityColumn.isLocalized()) {
						maxLength = 4000;
					}

					if (maxLength < 4000) {
						sb.append("VARCHAR(");
						sb.append(maxLength);
						sb.append(")");
					}
					else if (maxLength == 4000) {
						sb.append("STRING");
					}
					else if (maxLength > 4000) {
						sb.append("TEXT");
					}
				}
				else if (type.equals("Date")) {
					sb.append("DATE");
				}
				else {
					sb.append("invalid");
				}

				if (entityColumn.isPrimary()) {
					sb.append(" not null");
				}
				else if (type.equals("Date") || type.equals("Map") ||
						 type.equals("String")) {

					sb.append(" null");
				}

				sb.append(",\n");
			}
		}

		if (entities[1].isChangeTrackingEnabled() &&
			entities[2].isChangeTrackingEnabled()) {

			sb.append("\tctCollectionId LONG default 0 not null,\n");
			sb.append("\tctChangeType BOOLEAN,\n");
		}
		else if (entities[1].isChangeTrackingEnabled() ||
				 entities[2].isChangeTrackingEnabled()) {

			throw new ServiceBuilderException(
				"Must enable change tracking for both sides of mapping table " +
					tableName);
		}

		sb.append("\tprimary key (");

		for (int i = 1; i < entities.length; i++) {
			Entity entity = entities[i];

			List<EntityColumn> pkEntityColumns = entity.getPKEntityColumns();

			for (int j = 0; j < pkEntityColumns.size(); j++) {
				EntityColumn entityColumn = pkEntityColumns.get(j);

				if ((i != 1) || (j != 0)) {
					sb.append(", ");
				}

				sb.append(entityColumn.getDBName());
			}
		}

		if (entities[1].isChangeTrackingEnabled() &&
			entities[2].isChangeTrackingEnabled()) {

			sb.append(", ctCollectionId");
		}

		sb.append(")\n");
		sb.append(");");

		return sb.toString();
	}

	/**
	 * @see #_createDSLTable(List, String, String, boolean, boolean)
	 * @see com.liferay.object.service.internal.petra.sql.dsl.DynamicObjectDefinitionTable#getCreateTableSQL
	 */
	private String _getCreateTableSQL(Entity entity) {
		List<EntityColumn> databaseRegularEntityColumns =
			entity.getDatabaseRegularEntityColumns();

		if (databaseRegularEntityColumns.isEmpty()) {
			return null;
		}

		StringBundler sb = new StringBundler();

		sb.append(_SQL_CREATE_TABLE);

		String tableName = entity.getTable();

		if ((_databaseNameMaxLength > 0) &&
			(tableName.length() > _databaseNameMaxLength)) {

			throw new ServiceBuilderException(
				StringBundler.concat(
					"Unable to create entity \"", tableName,
					"\" because table name exceeds ", _databaseNameMaxLength,
					" characters. Some databases do not allow table names ",
					"longer than 30 characters. To disable this warning set ",
					"the \"service-builder\" attribute \"database-name-max-",
					"length\" to the max length that your database supports."));
		}

		sb.append(tableName);

		sb.append(" (\n");

		for (int i = 0; i < databaseRegularEntityColumns.size(); i++) {
			EntityColumn entityColumn = databaseRegularEntityColumns.get(i);

			String dbName = entityColumn.getDBName();

			if ((_databaseNameMaxLength > 0) &&
				(dbName.length() > _databaseNameMaxLength)) {

				throw new ServiceBuilderException(
					StringBundler.concat(
						"Unable to create entity \"", tableName,
						"\" because column name \"", dbName, "\" exceeds ",
						_databaseNameMaxLength, " characters. Some databases ",
						"do not allow column names longer than 30 characters. ",
						"To disable this warning set the \"service-builder\" ",
						"attribute \"database-name-max-length\" to the max ",
						"length that your database supports"));
			}

			String type = entityColumn.getType();
			String idType = entityColumn.getIdType();

			sb.append("\t");
			sb.append(dbName);
			sb.append(" ");

			if (StringUtil.equalsIgnoreCase(type, "boolean")) {
				sb.append("BOOLEAN");
			}
			else if (StringUtil.equalsIgnoreCase(type, "double") ||
					 StringUtil.equalsIgnoreCase(type, "float")) {

				sb.append("DOUBLE");
			}
			else if (type.equals("int") || type.equals("Integer") ||
					 StringUtil.equalsIgnoreCase(type, "short")) {

				sb.append("INTEGER");
			}
			else if (StringUtil.equalsIgnoreCase(type, "long")) {
				sb.append("LONG");
			}
			else if (type.equals("BigDecimal")) {
				if (isVersionGTE_7_4_0()) {
					sb.append("BIGDECIMAL");
				}
				else {
					Map<String, String> hints = ModelHintsUtil.getHints(
						_apiPackagePath + ".model." + entity.getName(),
						entityColumn.getModelHintsName());

					String precision = "30";
					String scale = "16";

					if (hints != null) {
						precision = hints.getOrDefault("precision", precision);
						scale = hints.getOrDefault("scale", scale);
					}

					sb.append("DECIMAL(");
					sb.append(precision);
					sb.append(", ");
					sb.append(scale);
					sb.append(")");
				}
			}
			else if (type.equals("Blob")) {
				sb.append("BLOB");
			}
			else if (type.equals("Date")) {
				sb.append("DATE");
			}
			else if (type.equals("Map")) {
				sb.append("TEXT");
			}
			else if (type.equals("String")) {
				int maxLength = getMaxLength(entity.getName(), entityColumn);

				if (entityColumn.isLocalized() && (maxLength < 4000)) {
					maxLength = 4000;
				}

				if (maxLength < 4000) {
					sb.append("VARCHAR(");
					sb.append(maxLength);
					sb.append(")");
				}
				else if (maxLength == 4000) {
					sb.append("STRING");
				}
				else if (maxLength > 4000) {
					sb.append("TEXT");
				}
			}
			else {
				sb.append("invalid");
			}

			if (entityColumn.isPrimary()) {
				sb.append(" not null");

				if (!entity.hasCompoundPK() &&
					!entity.isChangeTrackingEnabled()) {

					sb.append(" primary key");
				}
			}
			else if (type.equals("BigDecimal") || type.equals("Date") ||
					 type.equals("Map") || type.equals("String")) {

				sb.append(" null");
			}

			if (Validator.isNotNull(idType) && idType.equals("identity")) {
				sb.append(" IDENTITY");
			}

			if (entity.isChangeTrackingEnabled() &&
				Objects.equals(entityColumn.getName(), "ctCollectionId")) {

				sb.append(" default 0 not null");
			}

			if (Objects.equals(entityColumn.getName(), "mvccVersion")) {
				sb.append(" default 0 not null");
			}

			if (((i + 1) != databaseRegularEntityColumns.size()) ||
				entity.hasCompoundPK() || entity.isChangeTrackingEnabled()) {

				sb.append(",");
			}

			sb.append("\n");
		}

		if (entity.hasCompoundPK() || entity.isChangeTrackingEnabled()) {
			sb.append("\tprimary key (");

			List<EntityColumn> pkEntityColumns = entity.getPKEntityColumns();

			for (int j = 0; j < pkEntityColumns.size(); j++) {
				EntityColumn pk = pkEntityColumns.get(j);

				sb.append(pk.getDBName());

				if ((j + 1) != pkEntityColumns.size()) {
					sb.append(", ");
				}
			}

			if (entity.isChangeTrackingEnabled()) {
				sb.append(", ctCollectionId");
			}

			sb.append(")\n");
		}

		sb.append(");");

		return sb.toString();
	}

	private String _getDeleteUADEntityMethodName(
		JavaClass javaClass, String entityName) {

		List<String> methodNames = new ArrayList<>();

		for (JavaMethod javaMethod : javaClass.getMethods(false)) {
			String javaMethodName = javaMethod.getName();

			if (javaMethodName.startsWith("delete")) {
				List<JavaType> javaTypes = javaMethod.getParameterTypes();

				if (javaTypes.size() == 1) {
					JavaType parameterType = javaTypes.get(0);

					if (StringUtil.equals(
							parameterType.getValue(), entityName)) {

						methodNames.add(javaMethodName);
					}
				}
			}
		}

		String deleteEntityMethodName = "delete" + entityName;

		if (methodNames.isEmpty() ||
			methodNames.contains(deleteEntityMethodName)) {

			return deleteEntityMethodName;
		}

		methodNames.sort(null);

		return methodNames.get(0);
	}

	private Entity _getEntityByTableName(String tableName) {
		for (Entity entity : _entities) {
			if (tableName.equals(entity.getTable())) {
				return entity;
			}
		}

		return null;
	}

	private EntityColumn _getEntityColumnByColumnDBName(
		Entity entity, String columnDBName) {

		EntityColumn entityColumn = _fetchEntityColumnByColumnDBName(
			entity, columnDBName);

		if (entityColumn != null) {
			return entityColumn;
		}

		throw new IllegalArgumentException(
			StringBundler.concat(
				"No entity column exist with column database name ",
				columnDBName, " for entity ", entity.getName()));
	}

	private String _getEntityExceptionName(
		Entity entity, boolean shortExceptionName) {

		String name = entity.getName();

		if (shortExceptionName) {
			String portletShortName = entity.getPortletShortName();

			if (Validator.isNull(portletShortName) ||
				(name.startsWith(portletShortName) &&
				 !name.equals(portletShortName))) {

				name = name.substring(portletShortName.length());
			}
		}

		return name;
	}

	private List<String> _getEntityMappingPKEntityColumnDBNames(
			EntityMapping entityMapping)
		throws Exception {

		if (entityMapping == null) {
			return null;
		}

		List<String> mappingPKEntityColumnDBNames = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			Entity entity = getEntity(entityMapping.getEntityName(i));

			if (entity == null) {
				return null;
			}

			String entityName = entity.getName();

			if (entityName.equals("Company")) {
				continue;
			}

			mappingPKEntityColumnDBNames.addAll(
				entity.getPKEntityColumnDBNames());
		}

		return mappingPKEntityColumnDBNames;
	}

	private JavaClass _getJavaClass(String fileName) throws Exception {
		fileName = _normalize(fileName);

		int pos = 0;

		if (fileName.startsWith(_implDirName)) {
			pos = _implDirName.length() + 1;
		}
		else if (fileName.startsWith(_apiDirName)) {
			pos = _apiDirName.length() + 1;
		}
		else {
			return null;
		}

		String fullyQualifiedClassName = StringUtil.replace(
			fileName.substring(pos, fileName.length() - 5), CharPool.SLASH,
			CharPool.PERIOD);

		JavaClass javaClass = _javaClasses.get(fullyQualifiedClassName);

		if (javaClass == null) {
			File file = new File(fileName);

			if (!file.exists()) {
				return null;
			}

			ClassLibraryBuilder classLibraryBuilder =
				new SortedClassLibraryBuilder();

			Class<?> clazz = getClass();

			classLibraryBuilder.appendClassLoader(clazz.getClassLoader());

			JavaProjectBuilder builder = new JavaProjectBuilder(
				classLibraryBuilder);

			builder.addSource(file);

			javaClass = builder.getClassByName(fullyQualifiedClassName);

			_javaClasses.put(fullyQualifiedClassName, javaClass);
		}

		return javaClass;
	}

	private String _getMethodKey(JavaMethod javaMethod) {
		StringBundler sb = new StringBundler();

		sb.append(getTypeGenericsName(javaMethod.getReturnType()));
		sb.append(StringPool.SPACE);
		sb.append(javaMethod.getName());
		sb.append(StringPool.OPEN_PARENTHESIS);

		List<JavaParameter> javaParameters = javaMethod.getParameters();

		for (JavaParameter javaParameter : javaParameters) {
			JavaType type = javaParameter.getType();

			sb.append(type.getGenericValue());

			sb.append(StringPool.COMMA);
		}

		if (!javaParameters.isEmpty()) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	private List<JavaMethod> _getMethods(JavaClass javaClass) {
		return _getMethods(javaClass, false);
	}

	private List<JavaMethod> _getMethods(
		JavaClass javaClass, boolean superclasses) {

		List<JavaMethod> methods = new ArrayList<>();

		List<String> cacheFieldMethods = new ArrayList<>();

		for (JavaField javaField : javaClass.getFields()) {
			List<JavaAnnotation> javaAnnotations = javaField.getAnnotations();

			for (JavaAnnotation javaAnnotation : javaAnnotations) {
				JavaClass javaAnnotationClass = javaAnnotation.getType();

				String className = javaAnnotationClass.getFullyQualifiedName();

				if (!className.equals(CacheField.class.getName())) {
					continue;
				}

				if (!GetterUtil.getBoolean(
						javaAnnotation.getNamedParameter(
							"propagateToInterface"))) {

					String methodName = null;

					Object namedParameter = javaAnnotation.getNamedParameter(
						"methodName");

					if (namedParameter != null) {
						methodName = StringUtil.unquote(
							StringUtil.trim(namedParameter.toString()));
					}

					if (Validator.isNull(methodName)) {
						methodName = TextFormatter.format(
							getVariableName(javaField), TextFormatter.G);
					}

					cacheFieldMethods.add("get".concat(methodName));
					cacheFieldMethods.add("set".concat(methodName));
				}

				break;
			}
		}

		for (JavaMethod javaMethod : javaClass.getMethods(superclasses)) {
			if (!cacheFieldMethods.contains(javaMethod.getName())) {
				methods.add(javaMethod);
			}
		}

		return methods;
	}

	private String _getMethodSignature(
		JavaMethod method, boolean useFullyQualifiedNames) {

		StringBundler sb = new StringBundler();

		sb.append(method.getName());
		sb.append(StringPool.OPEN_PARENTHESIS);

		for (JavaParameter parameter : method.getParameters()) {
			JavaType type = parameter.getType();

			String parameterValue = type.getFullyQualifiedName();

			if (!useFullyQualifiedNames) {
				int pos = parameterValue.lastIndexOf(CharPool.PERIOD);

				if (pos != -1) {
					parameterValue = parameterValue.substring(pos + 1);
				}
			}

			sb.append(parameterValue);
			sb.append(StringPool.COMMA);
		}

		if (sb.index() > 2) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	private String _getSessionTypeName(int sessionType) {
		if (sessionType == _SESSION_TYPE_LOCAL) {
			return "Local";
		}

		return "";
	}

	private String _getSpringNamespacesDeclarations() {
		StringBundler sb = new StringBundler(_springNamespaces.length * 6);

		for (String namespace : _springNamespaces) {
			sb.append("\txmlns");

			if (!_SPRING_NAMESPACE_BEANS.equals(namespace)) {
				sb.append(":");
				sb.append(namespace);
			}

			sb.append("=\"http://www.springframework.org/schema/");
			sb.append(namespace);
			sb.append("\"\n");
		}

		return sb.toString();
	}

	private String _getSpringSchemaLocations() {
		StringBundler sb = new StringBundler(_springNamespaces.length * 7);

		for (String namespace : _springNamespaces) {
			sb.append(" http://www.springframework.org/schema/");
			sb.append(namespace);
			sb.append(" http://www.springframework.org/schema/");
			sb.append(namespace);
			sb.append("/spring-");
			sb.append(namespace);
			sb.append(".xsd");
		}

		return sb.toString();
	}

	private String _getTplProperty(String key, String defaultValue) {
		return System.getProperty("service.tpl." + key, defaultValue);
	}

	private List<String> _getTransients(Entity entity, boolean parent)
		throws Exception {

		File modelFile = null;

		if (parent) {
			modelFile = new File(
				StringBundler.concat(
					_outputPath, "/model/impl/", entity.getName(),
					"ModelImpl.java"));
		}
		else {
			modelFile = new File(
				StringBundler.concat(
					_outputPath, "/model/impl/", entity.getName(),
					"Impl.java"));
		}

		String content = _read(modelFile);

		Matcher matcher = _getterPattern.matcher(content);

		Set<String> getters = new HashSet<>();

		while (!matcher.hitEnd()) {
			boolean found = matcher.find();

			if (found) {
				String property = matcher.group();

				if (property.contains("get")) {
					property = property.substring(
						property.indexOf("get") + 3, property.length() - 1);
				}
				else {
					property = property.substring(
						property.indexOf("is") + 2, property.length() - 1);
				}

				if (!entity.hasEntityColumn(property) &&
					!entity.hasEntityColumn(
						Introspector.decapitalize(property))) {

					property = Introspector.decapitalize(property);

					getters.add(property);
				}
			}
		}

		matcher = _setterPattern.matcher(content);

		Set<String> setters = new HashSet<>();

		while (!matcher.hitEnd()) {
			boolean found = matcher.find();

			if (found) {
				String property = matcher.group();

				property = property.substring(
					property.indexOf("set") + 3, property.length() - 1);

				if (!entity.hasEntityColumn(property) &&
					!entity.hasEntityColumn(
						Introspector.decapitalize(property))) {

					property = Introspector.decapitalize(property);

					setters.add(property);
				}
			}
		}

		getters.retainAll(setters);

		List<String> transients = new ArrayList<>(getters);

		Collections.sort(transients);

		return transients;
	}

	private List<Path> _getUpdateSQLFilePaths() throws Exception {
		if (!_osgiModule) {
			List<Path> updateSQLFilePaths = new ArrayList<>();

			try (DirectoryStream<Path> paths = Files.newDirectoryStream(
					Paths.get(_sqlDirName), "update-7.0.0-7.0.1*.sql")) {

				for (Path path : paths) {
					updateSQLFilePaths.add(path);
				}
			}

			return updateSQLFilePaths;
		}

		final AtomicReference<Path> atomicReference = new AtomicReference<>();

		FileSystem fileSystem = FileSystems.getDefault();

		final PathMatcher pathMatcher = fileSystem.getPathMatcher(
			"glob:**/dependencies/update.sql");

		Files.walkFileTree(
			Paths.get(_resourcesDirName),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					if (!pathMatcher.matches(path)) {
						return FileVisitResult.CONTINUE;
					}

					Path updateSQLFilePath = atomicReference.get();

					if (updateSQLFilePath == null) {
						atomicReference.set(path);
					}
					else {
						Version updateSQLFileVersion = _getUpdateSQLFileVersion(
							updateSQLFilePath);
						Version version = _getUpdateSQLFileVersion(path);

						if (updateSQLFileVersion.compareTo(version) < 0) {
							atomicReference.set(path);
						}
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return Arrays.asList(atomicReference.get());
	}

	private Version _getUpdateSQLFileVersion(Path path) {
		path = path.getName(path.getNameCount() - 3);

		String version = path.toString();

		version = StringUtil.replace(version, '_', '.');
		version = version.substring(1);

		return Version.getInstance(version);
	}

	private boolean _hasFinderThatIsNotUniqueByCompany(
		List<Element> columnElements, List<Element> finderColumnElements) {

		if (!isVersionGTE_7_3_0()) {
			return false;
		}

		boolean hasCompanyId = false;

		for (Element columnElement : columnElements) {
			String columnName = columnElement.attributeValue("name");

			if (columnName.equals("companyId")) {
				hasCompanyId = true;

				break;
			}
		}

		if (!hasCompanyId) {
			return false;
		}

		List<String> finderColumnNames = TransformUtil.transform(
			finderColumnElements,
			finderColumnElement -> {
				String finderColumnName = finderColumnElement.attributeValue(
					"name");

				if ((finderColumnName == null) ||
					(!finderColumnName.endsWith("Id") &&
					 !finderColumnName.endsWith("PK"))) {

					return null;
				}

				return finderColumnName;
			});

		if ((finderColumnNames.size() == 1) &&
			Objects.equals(finderColumnNames.get(0), "classNameId")) {

			return true;
		}

		return false;
	}

	private boolean _hasHttpMethods(JavaClass javaClass) {
		for (JavaMethod javaMethod : _getMethods(javaClass)) {
			if (javaMethod.isPublic() && isCustomMethod(javaMethod)) {
				return true;
			}
		}

		return false;
	}

	private boolean _hasLocalChanges(File propsFile) throws Exception {
		for (String localChangesFileName :
				GitUtil.getLocalChangesFileNames("")) {

			if (localChangesFileName.equals(propsFile.getPath())) {
				return true;
			}
		}

		return false;
	}

	private boolean _hasModifiedSQLFiles() {
		for (String modifiedFileName : _modifiedFileNames) {
			if (modifiedFileName.endsWith(".sql")) {
				return true;
			}
		}

		return false;
	}

	private boolean _isStringLocaleMap(JavaParameter javaParameter) {
		JavaType type = javaParameter.getType();

		if (!(type instanceof DefaultJavaParameterizedType)) {
			return false;
		}

		DefaultJavaParameterizedType defaultJavaParameterizedType =
			(DefaultJavaParameterizedType)type;

		List<JavaType> actualTypeArguments =
			defaultJavaParameterizedType.getActualTypeArguments();

		if ((actualTypeArguments.size() != 2) ||
			!_isTypeValue(actualTypeArguments.get(0), Locale.class.getName()) ||
			!_isTypeValue(actualTypeArguments.get(1), String.class.getName())) {

			return false;
		}

		return true;
	}

	private boolean _isTargetEntity(Entity entity) {
		if ((_targetEntityName == null) || _targetEntityName.startsWith("$")) {
			return true;
		}

		return _targetEntityName.equals(entity.getName());
	}

	private boolean _isTypeValue(JavaType type, String value) {
		return value.equals(type.getFullyQualifiedName());
	}

	private List<JavaAnnotation> _mergeAnnotations(
		List<JavaAnnotation> javaAnnotations1,
		List<JavaAnnotation> javaAnnotations2) {

		Map<JavaType, JavaAnnotation> javaAnnotationsMap = new HashMap<>();

		for (JavaAnnotation javaAnnotation : javaAnnotations2) {
			javaAnnotationsMap.put(javaAnnotation.getType(), javaAnnotation);
		}

		for (JavaAnnotation javaAnnotation : javaAnnotations1) {
			javaAnnotationsMap.put(javaAnnotation.getType(), javaAnnotation);
		}

		List<JavaAnnotation> javaAnnotations3 = new ArrayList<>(
			javaAnnotationsMap.values());

		Comparator<JavaAnnotation> comparator =
			new Comparator<JavaAnnotation>() {

				@Override
				public int compare(
					JavaAnnotation javaAnnotation1,
					JavaAnnotation javaAnnotation2) {

					String javaAnnotationString1 = javaAnnotation1.toString();
					String javaAnnotationString2 = javaAnnotation2.toString();

					return javaAnnotationString1.compareTo(
						javaAnnotationString2);
				}

			};

		Collections.sort(javaAnnotations3, comparator);

		return javaAnnotations3;
	}

	private List<JavaMethod> _mergeMethods(
		List<JavaMethod> javaMethods1, List<JavaMethod> javaMethods2,
		boolean mergeAnnotations) {

		Map<String, JavaMethod> javaMethodMap = new HashMap<>();

		for (JavaMethod javaMethod : javaMethods2) {
			javaMethodMap.put(_getMethodKey(javaMethod), javaMethod);
		}

		for (JavaMethod javaMethod : javaMethods1) {
			String javaMethodKey = _getMethodKey(javaMethod);

			JavaMethod existingJavaMethod = javaMethodMap.get(javaMethodKey);

			if (existingJavaMethod == null) {
				javaMethodMap.put(javaMethodKey, javaMethod);
			}
			else if (mergeAnnotations &&
					 (existingJavaMethod instanceof DefaultJavaMethod)) {

				DefaultJavaMethod newJavaMethod =
					(DefaultJavaMethod)existingJavaMethod;

				List<JavaAnnotation> javaAnnotations = _mergeAnnotations(
					javaMethod.getAnnotations(),
					existingJavaMethod.getAnnotations());

				newJavaMethod.setAnnotations(javaAnnotations);

				javaMethodMap.put(javaMethodKey, newJavaMethod);
			}
		}

		List<JavaMethod> javaMethods3 = new ArrayList<>(javaMethodMap.values());

		Comparator<JavaMethod> comparator = new Comparator<JavaMethod>() {

			@Override
			public int compare(JavaMethod javaMethod1, JavaMethod javaMethod2) {
				String methodSignature1 = _getMethodSignature(
					javaMethod1, false);
				String methodSignature2 = _getMethodSignature(
					javaMethod2, false);

				if (methodSignature1.equals(methodSignature2)) {
					methodSignature1 = _getMethodSignature(javaMethod1, true);
					methodSignature2 = _getMethodSignature(javaMethod2, true);
				}

				return methodSignature1.compareToIgnoreCase(methodSignature2);
			}

		};

		Collections.sort(javaMethods3, comparator);

		return javaMethods3;
	}

	private List<Entity> _mergeReferenceEntities(Entity entity) {
		List<Entity> referenceEntities = entity.getReferenceEntities();

		Set<Entity> set = new LinkedHashSet<>();

		if (_autoImportDefaultReferences) {
			set.addAll(_entities);
		}
		else {
			set.add(entity);
		}

		set.addAll(referenceEntities);

		return new ArrayList<>(set);
	}

	private void _mkdir(File dir) throws IOException {
		Files.createDirectories(dir.toPath());
	}

	private void _move(File sourceFile, File destinationFile) throws Exception {
		File parentFile = destinationFile.getParentFile();

		Path parentPath = parentFile.toPath();

		if (!Files.exists(parentPath)) {
			Files.createDirectories(parentPath);
		}

		Files.move(sourceFile.toPath(), destinationFile.toPath());
	}

	private String _normalize(String fileName) {
		return StringUtil.replace(
			fileName, CharPool.BACK_SLASH, CharPool.SLASH);
	}

	private List<IndexMetadata> _optimizeForBTreeIndexes(
		List<IndexMetadata> indexMetadatas) {

		Map<String, IntegerWrapper> frequencyMap = new HashMap<>();

		for (IndexMetadata indexMetadata : indexMetadatas) {
			for (String columnName : indexMetadata.getColumnNames()) {
				IntegerWrapper count = frequencyMap.computeIfAbsent(
					columnName, key -> new IntegerWrapper());

				if (columnName.endsWith("Date")) {
					count.setValue(0);
				}
				else {
					count.increment();
				}
			}
		}

		for (IndexMetadata indexMetadata : indexMetadatas) {
			indexMetadata.optimizeColumns(frequencyMap);
		}

		List<IndexMetadata> optimizedIndexMetadatas = new ArrayList<>();

		for (IndexMetadata indexMetadata : indexMetadatas) {
			_addIndexMetadata(optimizedIndexMetadatas, indexMetadata);
		}

		return optimizedIndexMetadatas;
	}

	private Entity _parseEntity(Element entityElement) throws Exception {
		String entityName = entityElement.attributeValue("name");
		String humanName = entityElement.attributeValue("human-name");
		String variableName = entityElement.attributeValue("variable-name");
		String pluralName = entityElement.attributeValue("plural-name");
		String pluralVariableName = entityElement.attributeValue(
			"plural-variable-name");

		String tableName = entityElement.attributeValue("table");

		if (Validator.isNull(tableName)) {
			tableName = entityName;

			if (_badTableNames.contains(entityName)) {
				tableName += StringPool.UNDERLINE;
			}

			if (_autoNamespaceTables) {
				tableName =
					_portletShortName + StringPool.UNDERLINE + entityName;
			}
		}

		boolean uuid = GetterUtil.getBoolean(
			entityElement.attributeValue("uuid"));
		boolean uuidAccessor = GetterUtil.getBoolean(
			entityElement.attributeValue("uuid-accessor"));

		String externalReferenceCode = GetterUtil.getString(
			entityElement.attributeValue("external-reference-code"), "none");

		externalReferenceCode = StringUtil.replace(
			externalReferenceCode, "false", "none");
		externalReferenceCode = StringUtil.replace(
			externalReferenceCode, "true", "company");

		boolean localService = GetterUtil.getBoolean(
			entityElement.attributeValue("local-service"));
		boolean remoteService = GetterUtil.getBoolean(
			entityElement.attributeValue("remote-service"), true);
		boolean persistence = GetterUtil.getBoolean(
			entityElement.attributeValue("persistence"), true);
		String persistenceClassName = GetterUtil.getString(
			entityElement.attributeValue("persistence-class"),
			StringBundler.concat(
				_packagePath, ".service.persistence.impl.", entityName,
				"PersistenceImpl"));

		String finderClassName = "";

		File originalFinderImplFile = new File(
			StringBundler.concat(
				_outputPath, "/service/persistence/", entityName,
				"FinderImpl.java"));
		File newFinderImplFile = new File(
			StringBundler.concat(
				_outputPath, "/service/persistence/impl/", entityName,
				"FinderImpl.java"));

		if (originalFinderImplFile.exists()) {
			_move(originalFinderImplFile, newFinderImplFile);

			String content = _read(newFinderImplFile);

			content = StringUtil.replace(
				content, "package " + _packagePath + ".service.persistence;",
				StringBundler.concat(
					"package ", _packagePath, ".service.persistence.impl;\n\n",
					"import ", _apiPackagePath, ".service.persistence.",
					entityName, "Finder;\n", "import ", _apiPackagePath,
					".service.persistence.", entityName, "Util;"));

			ToolsUtil.writeFileRaw(
				newFinderImplFile, content, _modifiedFileNames);
		}

		if (newFinderImplFile.exists()) {
			finderClassName = StringBundler.concat(
				_packagePath, ".service.persistence.impl.", entityName,
				"FinderImpl");
		}

		String dataSource = entityElement.attributeValue("data-source");
		String sessionFactory = entityElement.attributeValue("session-factory");
		String txManager = entityElement.attributeValue("tx-manager");
		boolean cacheEnabled = GetterUtil.getBoolean(
			entityElement.attributeValue("cache-enabled"), true);
		boolean changeTrackingEnabled = GetterUtil.getBoolean(
			entityElement.attributeValue("change-tracking-enabled"),
			_changeTrackingEnabled);

		boolean mvccEnabled = GetterUtil.getBoolean(
			entityElement.attributeValue("mvcc-enabled"), _mvccEnabled);

		boolean dynamicUpdateEnabled = GetterUtil.getBoolean(
			entityElement.attributeValue("dynamic-update-enabled"),
			mvccEnabled);

		boolean jsonEnabled = GetterUtil.getBoolean(
			entityElement.attributeValue("json-enabled"), remoteService);
		boolean trashEnabled = GetterUtil.getBoolean(
			entityElement.attributeValue("trash-enabled"));

		String uadApplicationName = GetterUtil.getString(
			entityElement.attributeValue("uad-application-name"),
			_portletShortName);

		uadApplicationName = TextFormatter.format(
			uadApplicationName, TextFormatter.D);

		boolean uadAutoDelete = GetterUtil.getBoolean(
			entityElement.attributeValue("uad-auto-delete"));

		String uadDirPath = GetterUtil.getString(
			entityElement.attributeValue("uad-dir-path"), _uadDirName);
		String uadPackagePath = GetterUtil.getString(
			entityElement.attributeValue("uad-package-path"), _packagePath);

		boolean versioned = GetterUtil.getBoolean(
			entityElement.attributeValue("versioned"));

		if (versioned) {
			mvccEnabled = GetterUtil.getBoolean(
				entityElement.attributeValue("mvcc-enabled"), true);

			if (!mvccEnabled) {
				throw new IllegalArgumentException(
					"Cannot use versioned entity with mvccEnabled disabled " +
						"for " + entityName);
			}

			if (!persistence) {
				throw new IllegalArgumentException(
					"Cannot use versioned entity with persistence disabled " +
						"for " + entityName);
			}
		}

		boolean deprecated = GetterUtil.getBoolean(
			entityElement.attributeValue("deprecated"));

		List<Element> columnElements = entityElement.elements("column");

		List<Element> derivedColumnElements = new ArrayList<>();

		if (mvccEnabled && !columnElements.isEmpty()) {
			Element columnElement = DocumentHelper.createElement("column");

			columnElement.addAttribute(
				"change-tracking-resolution-type", "CONTROL");
			columnElement.addAttribute("name", "mvccVersion");
			columnElement.addAttribute("type", "long");

			derivedColumnElements.add(columnElement);
		}

		if (changeTrackingEnabled && !columnElements.isEmpty()) {
			Element columnElement = DocumentHelper.createElement("column");

			columnElement.addAttribute(
				"change-tracking-resolution-type", "CONTROL");
			columnElement.addAttribute("name", "ctCollectionId");
			columnElement.addAttribute("type", "long");

			derivedColumnElements.add(columnElement);
		}

		if (uuid) {
			Element columnElement = DocumentHelper.createElement("column");

			columnElement.addAttribute(
				"change-tracking-resolution-type", "STRICT");
			columnElement.addAttribute("name", "uuid");
			columnElement.addAttribute("type", "String");

			derivedColumnElements.add(columnElement);
		}

		if (columnElements.contains(
				new EntityColumn(this, "externalReferenceCode"))) {

			externalReferenceCode = "none";
		}

		if (!StringUtil.equals(externalReferenceCode, "none")) {
			Element columnElement = DocumentHelper.createElement("column");

			columnElement.addAttribute(
				"change-tracking-resolution-type", "STRICT");
			columnElement.addAttribute("name", "externalReferenceCode");
			columnElement.addAttribute("type", "String");

			derivedColumnElements.add(columnElement);
		}

		if (versioned) {
			Element columnElement = DocumentHelper.createElement("column");

			columnElement.addAttribute(
				"change-tracking-resolution-type", "STRICT");
			columnElement.addAttribute("name", "headId");
			columnElement.addAttribute("type", "long");

			derivedColumnElements.add(columnElement);
		}

		Element localizedEntityElement = entityElement.element(
			"localized-entity");

		if (localizedEntityElement != null) {
			Element columnElement = DocumentHelper.createElement("column");

			columnElement.addAttribute(
				"change-tracking-resolution-type", "STRICT");
			columnElement.addAttribute("name", "defaultLanguageId");
			columnElement.addAttribute("type", "String");

			derivedColumnElements.add(columnElement);

			if (!persistence) {
				throw new IllegalArgumentException(
					"Cannot use localized-entity with persistence disabled " +
						"for " + entityName);
			}
		}

		List<EntityColumn> pkEntityColumns = new ArrayList<>();
		List<EntityColumn> regularEntityColumns = new ArrayList<>();
		List<EntityColumn> blobEntityColumns = new ArrayList<>();
		List<EntityColumn> collectionEntityColumns = new ArrayList<>();
		List<EntityColumn> entityColumns = new ArrayList<>();

		boolean permissionedModel = false;
		boolean resourcedModel = false;

		columnElements.addAll(0, derivedColumnElements);

		for (Element columnElement : columnElements) {
			String columnName = columnElement.attributeValue("name");

			String columnPluralName = columnElement.attributeValue(
				"plural-name");

			String columnDBName = columnElement.attributeValue("db-name");

			if (Validator.isNull(columnDBName)) {
				columnDBName = columnName;

				if (_badColumnNames.contains(columnName)) {
					columnDBName += StringPool.UNDERLINE;
				}
			}

			String columnMethodName = columnElement.attributeValue(
				"method-name");
			String columnType = columnElement.attributeValue("type");
			boolean primary = GetterUtil.getBoolean(
				columnElement.attributeValue("primary"));
			boolean accessor = GetterUtil.getBoolean(
				columnElement.attributeValue("accessor"));
			boolean filterPrimary = GetterUtil.getBoolean(
				columnElement.attributeValue("filter-primary"));
			String columnEntityName = columnElement.attributeValue("entity");

			String mappingTableName = columnElement.attributeValue(
				"mapping-table");

			if (Validator.isNotNull(mappingTableName)) {
				if (!persistence) {
					throw new IllegalArgumentException(
						"Cannot have mapping-table without persistence for " +
							"entity " + entityName);
				}

				if (_badTableNames.contains(mappingTableName)) {
					mappingTableName += StringPool.UNDERLINE;
				}

				if (_autoNamespaceTables) {
					mappingTableName =
						_portletShortName + StringPool.UNDERLINE +
							mappingTableName;
				}
			}

			String idType = columnElement.attributeValue("id-type");
			String idParam = columnElement.attributeValue("id-param");
			boolean convertNull = GetterUtil.getBoolean(
				columnElement.attributeValue("convert-null"), true);
			boolean lazy = GetterUtil.getBoolean(
				columnElement.attributeValue("lazy"), true);
			boolean localized = GetterUtil.getBoolean(
				columnElement.attributeValue("localized"));
			boolean colJsonEnabled = GetterUtil.getBoolean(
				columnElement.attributeValue("json-enabled"), jsonEnabled);

			String changeTrackingResolutionType = "merge";

			if (primary) {
				changeTrackingResolutionType = "pk";
			}
			else if (columnName.equals("classNameId") ||
					 columnName.equals("classPK") ||
					 columnName.equals("companyId") ||
					 columnName.equals("createDate") ||
					 columnName.equals("creatorUserId") ||
					 columnName.equals("groupId") ||
					 columnName.equals("userId") ||
					 columnName.equals("userName")) {

				changeTrackingResolutionType = "strict";
			}
			else if (columnName.equals("modifiedDate") &&
					 columnType.equals("Date")) {

				changeTrackingResolutionType = "ignore";
			}

			changeTrackingResolutionType = StringUtil.toUpperCase(
				GetterUtil.getString(
					columnElement.attributeValue(
						"change-tracking-resolution-type"),
					changeTrackingResolutionType));

			CTColumnResolutionType ctColumnResolutionType =
				CTColumnResolutionType.valueOf(changeTrackingResolutionType);

			boolean containerModel = GetterUtil.getBoolean(
				columnElement.attributeValue("container-model"));
			boolean parentContainerModel = GetterUtil.getBoolean(
				columnElement.attributeValue("parent-container-model"));
			String uadAnonymizeFieldName = GetterUtil.getString(
				columnElement.attributeValue("uad-anonymize-field-name"));
			boolean uadNonanonymizable = GetterUtil.getBoolean(
				columnElement.attributeValue("uad-nonanonymizable"));

			if (columnName.equals("resourceBlockId") &&
				!entityName.equals("ResourceBlock")) {

				permissionedModel = true;
			}

			if (columnName.equals("resourcePrimKey") && !primary) {
				resourcedModel = true;
			}

			EntityColumn entityColumn = new EntityColumn(
				this, columnName, columnPluralName, columnDBName,
				columnMethodName, columnType, primary, accessor, filterPrimary,
				columnEntityName, mappingTableName, idType, idParam,
				convertNull, lazy, localized, colJsonEnabled,
				ctColumnResolutionType, containerModel, parentContainerModel,
				uadAnonymizeFieldName, uadNonanonymizable);

			if (primary) {
				if (!columnType.equals("int") && !columnType.equals("long") &&
					!columnType.equals("String")) {

					throw new IllegalArgumentException(
						StringBundler.concat(
							"Primary key ", columnName, " of entity ",
							entityName, " must be an int, long, or String"));
				}

				pkEntityColumns.add(entityColumn);
			}

			if (columnType.equals("Collection")) {
				collectionEntityColumns.add(entityColumn);
			}
			else {
				regularEntityColumns.add(entityColumn);

				if (columnType.equals("Blob")) {
					blobEntityColumns.add(entityColumn);
				}
			}

			entityColumns.add(entityColumn);

			if (Validator.isNotNull(columnEntityName) &&
				Validator.isNotNull(mappingTableName)) {

				EntityMapping entityMapping = new EntityMapping(
					mappingTableName, entityName, columnEntityName);

				if (!_entityMappings.containsKey(mappingTableName)) {
					_entityMappings.put(mappingTableName, entityMapping);
				}
			}
		}

		if (uuid && pkEntityColumns.isEmpty()) {
			throw new ServiceBuilderException(
				"Unable to create entity \"" + entityName +
					"\" with a UUID without a primary key");
		}

		EntityOrder entityOrder = null;

		Element orderElement = entityElement.element("order");

		if (orderElement != null) {
			boolean asc = true;

			if ((orderElement.attribute("by") != null) &&
				Objects.equals(orderElement.attributeValue("by"), "desc")) {

				asc = false;
			}

			List<EntityColumn> orderEntityColumns = new ArrayList<>();

			entityOrder = new EntityOrder(asc, orderEntityColumns);

			List<Element> orderColumnElements = orderElement.elements(
				"order-column");

			for (Element orderColumnElement : orderColumnElements) {
				String orderColName = orderColumnElement.attributeValue("name");
				boolean orderColCaseSensitive = GetterUtil.getBoolean(
					orderColumnElement.attributeValue("case-sensitive"), true);

				boolean orderColByAscending = asc;

				String orderColBy = GetterUtil.getString(
					orderColumnElement.attributeValue("order-by"));

				if (orderColBy.equals("asc")) {
					orderColByAscending = true;
				}
				else if (orderColBy.equals("desc")) {
					orderColByAscending = false;
				}

				int index = entityColumns.indexOf(
					new EntityColumn(this, orderColName));

				if (index < 0) {
					throw new IllegalArgumentException(
						"Invalid order by column " + orderColName);
				}

				EntityColumn entityColumn = entityColumns.get(index);

				entityColumn.setOrderColumn(true);

				entityColumn = (EntityColumn)entityColumn.clone();

				entityColumn.setCaseSensitive(orderColCaseSensitive);
				entityColumn.setOrderByAscending(orderColByAscending);

				orderEntityColumns.add(entityColumn);
			}
		}

		List<EntityFinder> entityFinders = new ArrayList<>();

		List<Element> finderElements = entityElement.elements("finder");

		if (uuid) {
			if (entityColumns.contains(new EntityColumn(this, "companyId"))) {
				Element finderElement = DocumentHelper.createElement("finder");

				finderElement.addAttribute("name", "Uuid_C");
				finderElement.addAttribute("return-type", "Collection");

				Element finderColumnElement = finderElement.addElement(
					"finder-column");

				finderColumnElement.addAttribute("name", "uuid");

				finderColumnElement = finderElement.addElement("finder-column");

				finderColumnElement.addAttribute("name", "companyId");

				finderElements.add(0, finderElement);
			}

			if (entityColumns.contains(new EntityColumn(this, "groupId"))) {
				Element finderElement = DocumentHelper.createElement("finder");

				if (entityName.equals("Layout")) {
					finderElement.addAttribute("name", "UUID_G_P");
				}
				else {
					finderElement.addAttribute("name", "UUID_G");
				}

				finderElement.addAttribute("return-type", entityName);
				finderElement.addAttribute("unique", "true");

				Element finderColumnElement = finderElement.addElement(
					"finder-column");

				finderColumnElement.addAttribute("name", "uuid");

				finderColumnElement = finderElement.addElement("finder-column");

				finderColumnElement.addAttribute("name", "groupId");

				if (entityName.equals("Layout")) {
					finderColumnElement = finderElement.addElement(
						"finder-column");

					finderColumnElement.addAttribute("name", "privateLayout");
				}

				finderElements.add(0, finderElement);
			}

			Element finderElement = DocumentHelper.createElement("finder");

			finderElement.addAttribute("name", "Uuid");
			finderElement.addAttribute("return-type", "Collection");

			Element finderColumnElement = finderElement.addElement(
				"finder-column");

			finderColumnElement.addAttribute("name", "uuid");

			finderElements.add(0, finderElement);
		}

		if (!StringUtil.equals(externalReferenceCode, "none")) {
			Element finderElement = DocumentHelper.createElement("finder");

			String externalReferenceCodeUpperCase = StringUtil.toUpperCase(
				externalReferenceCode);

			if (isVersionGTE_7_4_0()) {
				finderElement.addAttribute(
					"name", "ERC_" + externalReferenceCodeUpperCase.charAt(0));
			}
			else {
				finderElement.addAttribute(
					"name", externalReferenceCodeUpperCase.charAt(0) + "_ERC");
			}

			finderElement.addAttribute("return-type", entityName);

			if (isVersionGTE_7_4_0()) {
				finderElement.addAttribute("unique", "true");
			}

			Element finderColumnElement = finderElement.addElement(
				"finder-column");

			if (isVersionGTE_7_4_0()) {
				finderColumnElement.addAttribute(
					"name", "externalReferenceCode");

				finderColumnElement = finderElement.addElement("finder-column");

				finderColumnElement.addAttribute(
					"name", externalReferenceCode + "Id");
			}
			else {
				finderColumnElement.addAttribute(
					"name", externalReferenceCode + "Id");

				finderColumnElement = finderElement.addElement("finder-column");

				finderColumnElement.addAttribute(
					"name", "externalReferenceCode");
			}

			finderElements.add(finderElement);
		}

		if (permissionedModel) {
			Element finderElement = DocumentHelper.createElement("finder");

			finderElement.addAttribute("name", "ResourceBlockId");
			finderElement.addAttribute("return-type", "Collection");

			Element finderColumnElement = finderElement.addElement(
				"finder-column");

			finderColumnElement.addAttribute("name", "resourceBlockId");

			finderElements.add(0, finderElement);
		}

		if (resourcedModel) {
			Element finderElement = DocumentHelper.createElement("finder");

			finderElement.addAttribute("name", "ResourcePrimKey");
			finderElement.addAttribute("return-type", "Collection");

			Element finderColumnElement = finderElement.addElement(
				"finder-column");

			finderColumnElement.addAttribute("name", "resourcePrimKey");

			finderElements.add(0, finderElement);
		}

		if (versioned) {
			Element finderElement = DocumentHelper.createElement("finder");

			finderElement.addAttribute("name", "HeadId");
			finderElement.addAttribute("return-type", entityName);
			finderElement.addAttribute("unique", "true");

			Element finderColumnElement = finderElement.addElement(
				"finder-column");

			finderColumnElement.addAttribute("name", "headId");

			finderElements.add(finderElement);
		}

		String alias = TextFormatter.format(entityName, TextFormatter.I);

		if (_badAliasNames.contains(StringUtil.toLowerCase(alias))) {
			alias += StringPool.UNDERLINE;
		}

		for (Element finderElement : finderElements) {
			String finderName = finderElement.attributeValue("name");
			String finderPluralName = finderElement.attributeValue(
				"plural-name");
			boolean finderPretouch = GetterUtil.getBoolean(
				finderElement.attributeValue("pretouch"));
			String finderReturn = finderElement.attributeValue("return-type");
			boolean finderUnique = GetterUtil.getBoolean(
				finderElement.attributeValue("unique"));

			if (isVersionGTE_7_4_0() &&
				!Objects.equals(finderReturn, "Collection") && !finderUnique &&
				ArrayUtil.contains(
					_FORCE_UNIQUE_FINDER_PACKAGE_PATHS, _packagePath)) {

				throw new IllegalArgumentException(
					StringBundler.concat(
						"Finder \"", finderName, "\" defined by entity \"",
						entityName, "\" must set unique=\"true\" as its ",
						"return type is the name of the entity"));
			}

			String finderWhere = finderElement.attributeValue("where");

			String finderDBWhere = finderWhere;

			if (Validator.isNotNull(finderWhere)) {
				for (EntityColumn column : entityColumns) {
					String name = column.getName();

					if (_containSpecialCharacter(name)) {
						finderWhere = StringUtil.replace(
							finderWhere, name, alias + "." + name);
						finderDBWhere = StringUtil.replace(
							finderDBWhere, name,
							alias + "." + column.getDBName());
					}
					else {
						finderWhere = finderWhere.replaceAll(
							"\\b" + name + "\\b", alias + "." + name);
						finderDBWhere = finderDBWhere.replaceAll(
							"\\b" + name + "\\b",
							alias + "." + column.getDBName());
					}
				}
			}

			boolean finderDBIndex = GetterUtil.getBoolean(
				finderElement.attributeValue("db-index"), true);

			List<EntityColumn> finderEntityColumns = new ArrayList<>();

			List<Element> finderColumnElements = finderElement.elements(
				"finder-column");

			for (Element finderColumnElement : finderColumnElements) {
				String finderColumnName = finderColumnElement.attributeValue(
					"name");
				boolean finderColCaseSensitive = GetterUtil.getBoolean(
					finderColumnElement.attributeValue("case-sensitive"), true);
				String finderColComparator = GetterUtil.getString(
					finderColumnElement.attributeValue("comparator"), "=");
				String finderColArrayableOperator = GetterUtil.getString(
					finderColumnElement.attributeValue("arrayable-operator"));
				boolean finderColArrayablePagination = GetterUtil.getBoolean(
					finderColumnElement.attributeValue("arrayable-pagination"));
				boolean finderColIndexable = GetterUtil.getBoolean(
					finderColumnElement.attributeValue("indexable"), true);

				EntityColumn entityColumn = Entity.getEntityColumn(
					finderColumnName, entityColumns);

				if (!entityColumn.isFinderPath()) {
					entityColumn.setFinderPath(true);
				}

				entityColumn = (EntityColumn)entityColumn.clone();

				entityColumn.setCaseSensitive(finderColCaseSensitive);
				entityColumn.setComparator(finderColComparator);
				entityColumn.setArrayableOperator(finderColArrayableOperator);
				entityColumn.setArrayablePagination(
					finderColArrayablePagination);
				entityColumn.setIndexable(finderColIndexable);

				entityColumn.validate();

				finderEntityColumns.add(entityColumn);
			}

			if (_hasFinderThatIsNotUniqueByCompany(
					columnElements, finderColumnElements)) {

				throw new IllegalArgumentException(
					StringBundler.concat(
						"Finder ", finderName, " for entity ", entityName,
						" needs an additional ID column to make it unique by ",
						"company"));
			}

			entityFinders.add(
				new EntityFinder(
					this, finderName, finderPluralName, finderPretouch,
					finderReturn, finderUnique, finderWhere, finderDBWhere,
					finderDBIndex, finderEntityColumns));
		}

		String uadOutputPath =
			uadDirPath + "/" + StringUtil.replace(uadPackagePath, '.', '/');

		List<Entity> referenceEntities = new ArrayList<>();
		List<String> unresolvedReferenceEntityNames = new ArrayList<>();

		if (_build) {
			Set<String> referenceEntityNames = new TreeSet<>();

			List<Element> referenceElements = entityElement.elements(
				"reference");

			for (Element referenceElement : referenceElements) {
				String referencePackagePath = referenceElement.attributeValue(
					"package-path");
				String referenceEntityName = referenceElement.attributeValue(
					"entity");

				referenceEntityNames.add(
					referencePackagePath + "." + referenceEntityName);
			}

			if (!_packagePath.equals("com.liferay.counter")) {
				referenceEntityNames.add("com.liferay.counter.Counter");
			}

			if (_autoImportDefaultReferences) {
				referenceEntityNames.add("com.liferay.portal.ClassName");
				referenceEntityNames.add("com.liferay.portal.Resource");
				referenceEntityNames.add("com.liferay.portal.User");
			}

			for (String referenceEntityName : referenceEntityNames) {
				try {
					Entity entity = getEntity(referenceEntityName);

					if (_dtdVersion.isPreviousVersionThan("7.1.0")) {

						// See LPS-76509. Added this hack for
						// c9c1fcef14c5cdc1325ae97fee79dbc138728c3c in 7.1.x.

						String apiPackagePath = entity.getApiPackagePath();

						if (apiPackagePath.equals(
								"com.liferay.message.boards")) {

							entity.setApiPackagePath(
								apiPackagePath + ".kernel");
						}
					}

					referenceEntities.add(entity);
				}
				catch (RuntimeException runtimeException) {
					unresolvedReferenceEntityNames.add(referenceEntityName);
				}
			}
		}

		List<String> txRequiredMethodNames = new ArrayList<>();

		List<Element> txRequiredElements = entityElement.elements(
			"tx-required");

		if (!txRequiredElements.isEmpty()) {
			System.err.println(
				"The tx-required attribute is deprecated in favor annotating " +
					"the service impl method with " +
						"com.liferay.portal.kernel.transaction.Transactional");

			for (Element txRequiredElement : txRequiredElements) {
				String txRequired = txRequiredElement.getText();

				txRequiredMethodNames.add(txRequired);
			}
		}

		boolean resourceActionModel = _resourceActionModels.contains(
			_apiPackagePath + ".model." + entityName);

		Entity entity = new Entity(
			this, _packagePath, _apiPackagePath, _portletShortName, entityName,
			variableName, pluralName, pluralVariableName, humanName, tableName,
			alias, uuid, uuidAccessor, externalReferenceCode, localService,
			remoteService, persistence, persistenceClassName, finderClassName,
			dataSource, sessionFactory, txManager, cacheEnabled,
			changeTrackingEnabled, dynamicUpdateEnabled, jsonEnabled,
			mvccEnabled, trashEnabled, uadApplicationName, uadAutoDelete,
			uadOutputPath, uadPackagePath, deprecated, pkEntityColumns,
			regularEntityColumns, blobEntityColumns, collectionEntityColumns,
			entityColumns, entityOrder, entityFinders, referenceEntities,
			unresolvedReferenceEntityNames, txRequiredMethodNames,
			resourceActionModel);

		if (changeTrackingEnabled && !columnElements.isEmpty()) {
			if (!mvccEnabled) {
				throw new ServiceBuilderException(
					"MVCC must be enabled to use change tracking for " +
						entityName);
			}

			if (entity.isHierarchicalTree()) {
				throw new ServiceBuilderException(
					"Change tracking with hierarchical tree is not supported " +
						"for " + entityName);
			}

			if (pkEntityColumns.size() > 1) {
				throw new ServiceBuilderException(
					"Compound primary key with change tracked columns is not " +
						"supported for " + entityName);
			}

			EntityColumn pkEntityColumn = pkEntityColumns.get(0);

			if (!Objects.equals(pkEntityColumn.getType(), "long")) {
				throw new ServiceBuilderException(
					"Primary key must be of type long to enable change " +
						"tracking for " + entityName);
			}

			if (pkEntityColumn.getCTColumnResolutionType() !=
					CTColumnResolutionType.PK) {

				throw new ServiceBuilderException(
					StringBundler.concat(
						"Illegal change-tracking-resolution-type ",
						pkEntityColumn.getCTColumnResolutionType(),
						" for entity ", entityName, " on column ",
						pkEntityColumn.getName()));
			}
		}

		_entities.add(entity);

		if (entity.isUADEnabled()) {
			if (!_uadApplicationEntities.containsKey(uadApplicationName)) {
				_uadApplicationEntities.put(
					uadApplicationName, ListUtil.fromArray(entity));
			}
			else {
				List<Entity> uadApplicationEntities =
					_uadApplicationEntities.get(uadApplicationName);

				uadApplicationEntities.add(entity);
			}
		}

		if (versioned) {
			_parseVersionEntity(entity, columnElements);
		}

		if (localizedEntityElement != null) {
			_parseLocalizedEntity(entity, localizedEntityElement);

			if (versioned) {
				Entity localizedEntity = entity.getLocalizedEntity();

				entity.addReferenceEntity(localizedEntity.getVersionEntity());
			}
		}

		if (versioned) {
			EntityColumn headEntityColumn = new EntityColumn(
				this, "head", null, "head", null, "boolean", false, false,
				false, null, null, null, null, true, false, false, false,
				CTColumnResolutionType.STRICT, false, false, null, false);

			headEntityColumn.setComparator("=");
			headEntityColumn.setFinderPath(true);
			headEntityColumn.setInterfaceColumn(false);

			List<EntityColumn> databaseRegularEntityColumns =
				entity.getDatabaseRegularEntityColumns();

			int index = databaseRegularEntityColumns.indexOf(
				new EntityColumn(this, "headId"));

			databaseRegularEntityColumns.add(index + 1, headEntityColumn);

			List<EntityColumn> entityFinderColumns =
				entity.getFinderEntityColumns();

			entityFinderColumns.add(headEntityColumn);

			Collections.sort(entityFinderColumns);

			String localizedPKEntityColumn = entityElement.attributeValue(
				"localized-pk-entity-column");

			ListIterator<EntityFinder> listIterator =
				entityFinders.listIterator();

			while (listIterator.hasNext()) {
				EntityFinder entityFinder = listIterator.next();

				String finderName = entityFinder.getName();

				if (finderName.equals("HeadId") ||
					((localizedPKEntityColumn != null) &&
					 entityFinder.hasEntityColumn(localizedPKEntityColumn))) {

					continue;
				}

				// Finder for both draft and head

				listIterator.set(
					new EntityFinder(
						this, entityFinder.getName(),
						entityFinder.getPluralName(), entityFinder.isPretouch(),
						"Collection", false, entityFinder.getWhere(),
						entityFinder.getDBWhere(), entityFinder.isDBIndex(),
						new ArrayList<>(entityFinder.getEntityColumns())));

				List<EntityColumn> finderEntityColumns =
					entityFinder.getEntityColumns();

				finderEntityColumns.add(headEntityColumn);

				// Finder for either draft or head

				listIterator.add(
					new EntityFinder(
						this, entityFinder.getName() + "_Head", null,
						entityFinder.isPretouch(), entityFinder.getReturnType(),
						entityFinder.isUnique(), entityFinder.getWhere(),
						entityFinder.getDBWhere(), entityFinder.isDBIndex(),
						finderEntityColumns));
			}
		}

		return entity;
	}

	private void _parseLocalizedEntity(
			Entity entity, Element localizedEntityElement)
		throws Exception {

		if (!entity.hasLocalService()) {
			throw new IllegalArgumentException(
				entity.getName() +
					" must have a local service to use localized entity");
		}

		for (EntityColumn entityColumn : entity.getEntityColumns()) {
			if (entityColumn.isLocalized()) {
				throw new IllegalArgumentException(
					StringBundler.concat(
						"Unable to use localized entity with localized column ",
						entityColumn.getName(), " in ", entity.getName()));
			}
		}

		// Localized entity

		Element newLocalizedEntityElement = DocumentHelper.createElement(
			"entity");

		if (entity.isChangeTrackingEnabled()) {
			newLocalizedEntityElement.addAttribute(
				"change-tracking-enabled", "true");
		}

		if (Validator.isNotNull(entity.getDataSource())) {
			newLocalizedEntityElement.addAttribute(
				"data-source", entity.getDataSource());
		}

		if (entity.isDeprecated()) {
			newLocalizedEntityElement.addAttribute("deprecated", "true");
		}

		newLocalizedEntityElement.addAttribute("local-service", "false");
		newLocalizedEntityElement.addAttribute("mvcc-enabled", "true");

		String localizedEntityName = GetterUtil.getString(
			localizedEntityElement.attributeValue("name"),
			entity.getName() + "Localization");

		newLocalizedEntityElement.addAttribute("name", localizedEntityName);

		newLocalizedEntityElement.addAttribute("remote-service", "false");

		if (Validator.isNotNull(entity.getSessionFactory())) {
			newLocalizedEntityElement.addAttribute(
				"session-factory", entity.getSessionFactory());
		}

		String localizedEntityTableName = localizedEntityElement.attributeValue(
			"table");

		if (Validator.isNotNull(localizedEntityTableName)) {
			newLocalizedEntityElement.addAttribute(
				"table", localizedEntityTableName);
		}

		if (Validator.isNotNull(entity.getTXManager())) {
			newLocalizedEntityElement.addAttribute(
				"tx-manager", entity.getTXManager());
		}

		if (entity.getVersionEntity() != null) {
			newLocalizedEntityElement.addAttribute("versioned", "true");
		}

		newLocalizedEntityElement.addAttribute("uuid", "false");

		// Auto generated columns

		Element newLocalizedColumnElement =
			newLocalizedEntityElement.addElement("column");

		newLocalizedColumnElement.addAttribute(
			"name",
			TextFormatter.format(localizedEntityName + "Id", TextFormatter.I));
		newLocalizedColumnElement.addAttribute("primary", "true");
		newLocalizedColumnElement.addAttribute("type", "long");

		if (entity.hasEntityColumn("companyId")) {
			newLocalizedColumnElement = newLocalizedEntityElement.addElement(
				"column");

			newLocalizedColumnElement.addAttribute("name", "companyId");
			newLocalizedColumnElement.addAttribute("type", "long");
		}

		List<EntityColumn> pkEntityColumns = entity.getPKEntityColumns();

		if (pkEntityColumns.size() > 1) {
			throw new IllegalArgumentException(
				"Unable to use localized entity with compound primary key");
		}

		EntityColumn pkEntityColumn = pkEntityColumns.get(0);

		if (entity.getVersionEntity() != null) {
			newLocalizedEntityElement.addAttribute(
				"localized-pk-entity-column", pkEntityColumn.getName());
		}

		newLocalizedColumnElement = newLocalizedEntityElement.addElement(
			"column");

		newLocalizedColumnElement.addAttribute(
			"name", pkEntityColumn.getName());
		newLocalizedColumnElement.addAttribute(
			"type", pkEntityColumn.getType());

		newLocalizedColumnElement = newLocalizedEntityElement.addElement(
			"column");

		newLocalizedColumnElement.addAttribute("name", "languageId");
		newLocalizedColumnElement.addAttribute("type", "String");

		// Localized columns

		List<Element> localizedColumnElements = localizedEntityElement.elements(
			"localized-column");

		if (localizedColumnElements.isEmpty()) {
			throw new IllegalArgumentException(
				"Unable to use localized entity table without localized " +
					"columns");
		}

		List<EntityColumn> localizedEntityColumns = new ArrayList<>(
			localizedColumnElements.size());

		for (Element localizedColumnElement : localizedColumnElements) {
			String columnName = localizedColumnElement.attributeValue("name");

			String columnDBName = localizedColumnElement.attributeValue(
				"db-name");

			if (Validator.isNull(columnDBName)) {
				columnDBName = columnName;

				if (_badColumnNames.contains(columnName)) {
					columnDBName += StringPool.UNDERLINE;
				}
			}

			localizedEntityColumns.add(
				new EntityColumn(this, columnName, columnDBName));

			newLocalizedColumnElement = newLocalizedEntityElement.addElement(
				"column");

			newLocalizedColumnElement.addAttribute("name", columnName);
			newLocalizedColumnElement.addAttribute(
				"change-tracking-resolution-type",
				localizedColumnElement.attributeValue(
					"change-tracking-resolution-type"));
			newLocalizedColumnElement.addAttribute("db-name", columnDBName);
			newLocalizedColumnElement.addAttribute("type", "String");
		}

		// Auto generated finders

		Element newLocalizedFinderElement =
			newLocalizedEntityElement.addElement("finder");

		String finderName = TextFormatter.format(
			pkEntityColumn.getName(), TextFormatter.G);

		newLocalizedFinderElement.addAttribute("name", finderName);

		newLocalizedFinderElement.addAttribute("return-type", "Collection");

		Element newLocalizedFinderColumnElement =
			newLocalizedFinderElement.addElement("finder-column");

		newLocalizedFinderColumnElement.addAttribute(
			"name", pkEntityColumn.getName());

		newLocalizedFinderElement = newLocalizedEntityElement.addElement(
			"finder");

		newLocalizedFinderElement.addAttribute(
			"name", finderName + "_LanguageId");

		newLocalizedFinderElement.addAttribute(
			"return-type", localizedEntityName);

		newLocalizedFinderElement.addAttribute("unique", "true");

		newLocalizedFinderColumnElement = newLocalizedFinderElement.addElement(
			"finder-column");

		newLocalizedFinderColumnElement.addAttribute(
			"name", pkEntityColumn.getName());

		newLocalizedFinderColumnElement = newLocalizedFinderElement.addElement(
			"finder-column");

		newLocalizedFinderColumnElement.addAttribute("name", "languageId");

		// Manual columns

		List<Element> columnElements = localizedEntityElement.elements(
			"column");

		for (Element columnElement : columnElements) {
			String localized = columnElement.attributeValue("localized");

			if (localized != null) {
				throw new IllegalArgumentException(
					"Unable to have localized columns in localized table for " +
						"entity " + entity.getName());
			}

			Element newColumnElement = newLocalizedEntityElement.addElement(
				"column", columnElement.getStringValue());

			List<Attribute> columnAttributes = columnElement.attributes();

			for (Attribute columnAttribute : columnAttributes) {
				newColumnElement.addAttribute(
					columnAttribute.getName(),
					columnAttribute.getStringValue());
			}
		}

		// Manual Order

		Element orderElement = localizedEntityElement.element("order");

		if (orderElement != null) {
			Element newOrderElement = newLocalizedEntityElement.addElement(
				"order", orderElement.getStringValue());

			List<Attribute> orderAttributes = orderElement.attributes();

			for (Attribute orderAttribute : orderAttributes) {
				newOrderElement.addAttribute(
					orderAttribute.getName(), orderAttribute.getStringValue());
			}
		}

		// Manual finders

		List<Element> finderElements = localizedEntityElement.elements(
			"finder");

		for (Element finderElement : finderElements) {
			Element newFinderElement = newLocalizedEntityElement.addElement(
				"finder", finderElement.getStringValue());

			List<Attribute> finderElementAttributes =
				finderElement.attributes();

			for (Attribute finderElementAttribute : finderElementAttributes) {
				newFinderElement.addAttribute(
					finderElementAttribute.getName(),
					finderElementAttribute.getStringValue());
			}

			List<Element> finderColumnElements = finderElement.elements(
				"finder-column");

			for (Element finderColumnElement : finderColumnElements) {
				List<Attribute> finderColumnAttributes =
					finderColumnElement.attributes();

				Element newFinderColumnElement = newFinderElement.addElement(
					"finder-column", finderColumnElement.getStringValue());

				for (Attribute finderColumnAttribute : finderColumnAttributes) {
					newFinderColumnElement.addAttribute(
						finderColumnAttribute.getName(),
						finderColumnAttribute.getStringValue());
				}
			}
		}

		entity.setLocalizedEntityColumns(localizedEntityColumns);
		entity.setLocalizedEntity(_parseEntity(newLocalizedEntityElement));
	}

	private void _parseVersionEntity(
			Entity entity, List<Element> columnElements)
		throws Exception {

		// Version entity

		Element versionEntityElement = DocumentHelper.createElement("entity");

		if (Validator.isNotNull(entity.getDataSource())) {
			versionEntityElement.addAttribute(
				"data-source", entity.getDataSource());
		}

		if (entity.isDeprecated()) {
			versionEntityElement.addAttribute("deprecated", "true");
		}

		versionEntityElement.addAttribute("local-service", "false");

		if (entity.isChangeTrackingEnabled()) {
			versionEntityElement.addAttribute("mvcc-enabled", "true");
		}
		else {
			versionEntityElement.addAttribute("mvcc-enabled", "false");
		}

		versionEntityElement.addAttribute("name", entity.getName() + "Version");

		versionEntityElement.addAttribute("remote-service", "false");

		if (Validator.isNotNull(entity.getSessionFactory())) {
			versionEntityElement.addAttribute(
				"session-factory", entity.getSessionFactory());
		}

		if (Validator.isNotNull(entity.getTXManager())) {
			versionEntityElement.addAttribute(
				"tx-manager", entity.getTXManager());
		}

		versionEntityElement.addAttribute("uuid", "false");

		// Version columns

		Element versionEntityColumnElement = versionEntityElement.addElement(
			"column");

		versionEntityColumnElement.addAttribute(
			"name", entity.getVariableName() + "VersionId");
		versionEntityColumnElement.addAttribute("primary", "true");
		versionEntityColumnElement.addAttribute("type", "long");

		List<EntityColumn> pkEntityColumns = entity.getPKEntityColumns();

		if (pkEntityColumns.size() > 1) {
			throw new IllegalArgumentException(
				"Unable to use versioned entity with compound primary key");
		}

		EntityColumn pkEntityColumn = pkEntityColumns.get(0);

		if (!Objects.equals(pkEntityColumn.getType(), "long")) {
			throw new IllegalArgumentException(
				"Must have long primary key to create versioned entity");
		}

		versionEntityColumnElement = versionEntityElement.addElement("column");

		versionEntityColumnElement.addAttribute("name", "version");
		versionEntityColumnElement.addAttribute("type", "int");

		// Copied columns

		for (Element columnElement : columnElements) {
			String name = columnElement.attributeValue("name");

			if (!name.equals("mvccVersion") && !name.equals("headId") &&
				(!name.equals("ctCollectionId") ||
				 !entity.isChangeTrackingEnabled())) {

				versionEntityColumnElement = versionEntityElement.addElement(
					"column");

				List<Attribute> columnAttributes = columnElement.attributes();

				for (Attribute attribute : columnAttributes) {
					String attributeName = attribute.getName();

					if (!attributeName.equals("primary") &&
						!attributeName.equals("uad-anonymize-field-name") &&
						!attributeName.equals("uad-nonanonymizable")) {

						versionEntityColumnElement.addAttribute(
							attributeName, attribute.getValue());
					}
				}
			}
		}

		// Order

		Element orderElement = versionEntityElement.addElement("order");

		Element orderColumnElement = orderElement.addElement("order-column");

		orderColumnElement.addAttribute("name", "version");
		orderColumnElement.addAttribute("order-by", "desc");

		// Finders

		Element versionFinderElement = versionEntityElement.addElement(
			"finder");

		String finderName = TextFormatter.format(
			pkEntityColumn.getName(), TextFormatter.G);

		versionFinderElement.addAttribute("name", finderName);

		versionFinderElement.addAttribute("return-type", "Collection");

		Element versionColumnElement = versionFinderElement.addElement(
			"finder-column");

		versionColumnElement.addAttribute("name", pkEntityColumn.getName());

		versionFinderElement = versionEntityElement.addElement("finder");

		versionFinderElement.addAttribute("name", finderName + "_Version");

		versionFinderElement.addAttribute(
			"return-type", entity.getName() + "Version");

		versionFinderElement.addAttribute("unique", "true");

		versionColumnElement = versionFinderElement.addElement("finder-column");

		versionColumnElement.addAttribute("name", pkEntityColumn.getName());

		versionColumnElement = versionFinderElement.addElement("finder-column");

		versionColumnElement.addAttribute("name", "version");

		// Copied finders

		for (EntityFinder entityFinder : entity.getEntityFinders()) {
			finderName = entityFinder.getName();

			if (finderName.equals("HeadId") || finderName.startsWith("ERC")) {
				continue;
			}

			versionFinderElement = versionEntityElement.addElement("finder");

			versionFinderElement.addAttribute("name", finderName);

			versionFinderElement.addAttribute("return-type", "Collection");

			for (EntityColumn entityColumn : entityFinder.getEntityColumns()) {
				versionColumnElement = versionFinderElement.addElement(
					"finder-column");

				versionColumnElement.addAttribute(
					"name", entityColumn.getName());
			}

			versionFinderElement = versionEntityElement.addElement("finder");

			versionFinderElement.addAttribute("name", finderName + "_Version");

			for (EntityColumn entityColumn : entityFinder.getEntityColumns()) {
				versionColumnElement = versionFinderElement.addElement(
					"finder-column");

				versionColumnElement.addAttribute(
					"name", entityColumn.getName());
			}

			versionColumnElement = versionFinderElement.addElement(
				"finder-column");

			versionColumnElement.addAttribute("name", "version");

			if (entityFinder.isUnique()) {
				versionFinderElement.addAttribute(
					"return-type", entity.getName() + "Version");

				versionFinderElement.addAttribute("unique", "true");
			}
			else {
				versionFinderElement.addAttribute("return-type", "Collection");
			}
		}

		Entity versionEntity = _parseEntity(versionEntityElement);

		entity.setVersionEntity(versionEntity);

		versionEntity.setVersionedEntity(entity);
	}

	private String _processTemplate(String name, Map<String, Object> context)
		throws Exception {

		_currentTplName = name;

		Configuration configuration = _getConfiguration();

		Template template = configuration.getTemplate(name);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.process(context, unsyncStringWriter);

		return StringUtil.removeChar(unsyncStringWriter.toString(), '\r');
	}

	private Map<String, Object> _putDeprecatedKeys(
		Map<String, Object> context, JavaClass javaClass) {

		Entity entity = (Entity)context.get("entity");

		context.put("classDeprecated", entity.isDeprecated());

		context.put("classDeprecatedComment", "");

		if (javaClass != null) {
			DocletTag tag = javaClass.getTagByName("deprecated");

			if (tag != null) {
				context.put("classDeprecated", true);
				context.put("classDeprecatedComment", tag.getValue());
			}
		}

		return context;
	}

	private String _read(File file) throws IOException {
		String s = new String(
			Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

		return StringUtil.replace(
			s, StringPool.RETURN_NEW_LINE, StringPool.NEW_LINE);
	}

	private String _read(String fileName) throws IOException {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		String resourceName = _TPL_ROOT + fileName;

		InputStream inputStream = classLoader.getResourceAsStream(resourceName);

		String content = StringUtil.read(inputStream);

		return StringUtil.replace(
			content, StringPool.RETURN_NEW_LINE, StringPool.NEW_LINE);
	}

	private Set<String> _readLines(String fileName) throws Exception {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		Set<String> lines = new HashSet<>();

		StringUtil.readLines(classLoader.getResourceAsStream(fileName), lines);

		return lines;
	}

	private void _removeActionableDynamicQuery(Entity entity) {
		File file = new File(
			StringBundler.concat(
				_oldServiceOutputPath, "/service/persistence/",
				entity.getName(), "ActionableDynamicQuery.java"));

		file.delete();

		file = new File(
			StringBundler.concat(
				_serviceOutputPath, "/service/persistence/", entity.getName(),
				"ActionableDynamicQuery.java"));

		file.delete();
	}

	@SuppressWarnings("unused")
	private void _removeBaseUADAnonymizer(Entity entity) {
		_deleteFile(
			StringBundler.concat(
				entity.getUADOutputPath(), "/uad/anonymizer/Base",
				entity.getName(), "UADAnonymizer.java"));
	}

	private void _removeBaseUADDisplay(Entity entity) {
		_deleteFile(
			StringBundler.concat(
				entity.getUADOutputPath(), "/uad/display/Base",
				entity.getName(), "UADDisplay.java"));
	}

	@SuppressWarnings("unused")
	private void _removeBaseUADExporter(Entity entity) {
		_deleteFile(
			StringBundler.concat(
				entity.getUADOutputPath(), "/uad/exporter/Base",
				entity.getName(), "UADExporter.java"));
	}

	private void _removeBlobModels(Entity entity, String outputPath) {
		for (EntityColumn entityColumn : _getBlobEntityColumns(entity)) {
			_deleteFile(
				StringBundler.concat(
					outputPath, "/model/", entity.getName(),
					entityColumn.getMethodName(), "BlobModel.java"));
		}
	}

	private void _removeEJBPK(Entity entity, String outputPath) {
		List<EntityColumn> pkEntityColumns = entity.getPKEntityColumns();

		if (pkEntityColumns.size() <= 1) {
			return;
		}

		_deleteFile(
			StringBundler.concat(
				outputPath, "/service/persistence/", entity.getPKClassName(),
				".java"));
	}

	private void _removeExportActionableDynamicQuery(Entity entity) {
		File file = new File(
			StringBundler.concat(
				_oldServiceOutputPath, "/service/persistence/",
				entity.getName(), "ExportActionableDynamicQuery.java"));

		file.delete();

		file = new File(
			StringBundler.concat(
				_serviceOutputPath, "/service/persistence/", entity.getName(),
				"ExportActionableDynamicQuery.java"));

		file.delete();
	}

	private void _removeExtendedModel(Entity entity, String outputPath) {
		_deleteFile(
			StringBundler.concat(
				outputPath, "/model/", entity.getName(), ".java"));
	}

	private void _removeFinder(Entity entity, String outputPath) {
		_deleteFile(
			StringBundler.concat(
				outputPath, "/service/persistence/", entity.getName(),
				"Finder.java"));
	}

	private void _removeFinderBaseImpl(Entity entity) {
		_deleteFile(
			StringBundler.concat(
				_outputPath, "/service/persistence/impl/", entity.getName(),
				"FinderBaseImpl.java"));
	}

	private void _removeFinderUtil(Entity entity, String outputPath) {
		_deleteFile(
			StringBundler.concat(
				outputPath, "/service/persistence/", entity.getName(),
				"FinderUtil.java"));
	}

	private void _removeModel(Entity entity, String outputPath) {
		_deleteFile(
			StringBundler.concat(
				outputPath, "/model/", entity.getName(), "Model.java"));
	}

	private void _removeModelClp(Entity entity, String outputPath) {
		_deleteFile(
			StringBundler.concat(
				outputPath, "/model/", entity.getName(), "Clp.java"));
	}

	private void _removeModelSoap(Entity entity, String outputPath) {
		_deleteFile(
			StringBundler.concat(
				outputPath, "/model/", entity.getName(), "Soap.java"));
	}

	private void _removeModelWrapper(Entity entity, String outputPath) {
		_deleteFile(
			StringBundler.concat(
				outputPath, "/model/", entity.getName(), "Wrapper.java"));
	}

	private void _removeOldServices(Entity entity) {
		_removeModelClp(entity, _oldServiceOutputPath);
		_removeServiceClp(entity, _SESSION_TYPE_LOCAL, _oldServiceOutputPath);
		_removeServiceClp(entity, _SESSION_TYPE_REMOTE, _oldServiceOutputPath);
		_removeServiceClpInvoker(entity, _SESSION_TYPE_LOCAL);
		_removeServiceClpInvoker(entity, _SESSION_TYPE_REMOTE);
		_removeServiceClpMessageListener(_oldServiceOutputPath);
		_removeServiceClpSerializer(_oldServiceOutputPath);

		if (_oldServiceOutputPath.equals(_serviceOutputPath)) {
			return;
		}

		_removeBlobModels(entity, _oldServiceOutputPath);
		_removeEJBPK(entity, _oldServiceOutputPath);
		_removeExtendedModel(entity, _oldServiceOutputPath);
		_removeFinder(entity, _oldServiceOutputPath);
		_removeFinderUtil(entity, _oldServiceOutputPath);
		_removeModel(entity, _oldServiceOutputPath);
		_removeModelSoap(entity, _oldServiceOutputPath);
		_removeModelWrapper(entity, _oldServiceOutputPath);
		_removePersistence(entity, _oldServiceOutputPath);
		_removePersistenceUtil(entity, _oldServiceOutputPath);
		_removeService(entity, _SESSION_TYPE_LOCAL, _oldServiceOutputPath);
		_removeService(entity, _SESSION_TYPE_REMOTE, _oldServiceOutputPath);
		_removeServiceUtil(entity, _SESSION_TYPE_LOCAL, _oldServiceOutputPath);
		_removeServiceUtil(entity, _SESSION_TYPE_REMOTE, _oldServiceOutputPath);
		_removeServiceWrapper(
			entity, _SESSION_TYPE_LOCAL, _oldServiceOutputPath);
		_removeServiceWrapper(
			entity, _SESSION_TYPE_REMOTE, _oldServiceOutputPath);
		_removeServletContextUtil(_serviceOutputPath);
	}

	private void _removePersistence(Entity entity, String outputPath) {
		_deleteFile(
			StringBundler.concat(
				outputPath, "/service/persistence/", entity.getName(),
				"Persistence.java"));
	}

	private void _removePersistenceUtil(Entity entity, String outputPath) {
		_deleteFile(
			StringBundler.concat(
				outputPath, "/service/persistence/", entity.getName(),
				"Util.java"));
	}

	private void _removeService(
		Entity entity, int sessionType, String outputPath) {

		_deleteFile(
			StringBundler.concat(
				outputPath, "/service/", entity.getName(),
				_getSessionTypeName(sessionType), "Service.java"));
	}

	private void _removeServiceBaseImpl(Entity entity, int sessionType) {
		_deleteFile(
			StringBundler.concat(
				_outputPath, "/service/base/", entity.getName(),
				_getSessionTypeName(sessionType), "ServiceBaseImpl.java"));
	}

	private void _removeServiceClp(
		Entity entity, int sessionType, String outputPath) {

		_deleteFile(
			StringBundler.concat(
				outputPath, "/service/", entity.getName(),
				_getSessionTypeName(sessionType), "ServiceClp.java"));
	}

	private void _removeServiceClpInvoker(Entity entity, int sessionType) {
		_deleteFile(
			StringBundler.concat(
				_outputPath, "/service/base/", entity.getName(),
				_getSessionTypeName(sessionType), "ServiceClpInvoker.java"));
	}

	private void _removeServiceClpMessageListener(String outputPath) {
		_deleteFile(outputPath + "/service/messaging/ClpMessageListener.java");
	}

	private void _removeServiceClpSerializer(String outputPath) {
		_deleteFile(outputPath + "/service/ClpSerializer.java");
	}

	private void _removeServiceHttp(Entity entity) {
		_deleteFile(
			StringBundler.concat(
				_outputPath, "/service/http/", entity.getName(),
				"ServiceHttp.java"));
	}

	private void _removeServiceImpl(Entity entity, int sessionType) {
		_deleteFile(
			StringBundler.concat(
				_outputPath, "/service/impl/", entity.getName(),
				_getSessionTypeName(sessionType), "ServiceImpl.java"));
	}

	private void _removeServiceJson(Entity entity) {
		File file = new File(
			StringBundler.concat(
				_outputPath, "/service/http/", entity.getName(),
				"ServiceJSON.java"));

		if (file.exists()) {
			System.out.println("Removing deprecated " + file);

			file.delete();
		}
	}

	private void _removeServiceJsonSerializer(Entity entity) {
		File file = new File(
			StringBundler.concat(
				_serviceOutputPath, "/service/http/", entity.getName(),
				"JSONSerializer.java"));

		if (file.exists()) {
			System.out.println("Removing deprecated " + file);

			file.delete();
		}
	}

	private void _removeServiceSoap(Entity entity) {
		_deleteFile(
			StringBundler.concat(
				_outputPath, "/service/http/", entity.getName(),
				"ServiceSoap.java"));
	}

	private void _removeServiceUtil(
		Entity entity, int sessionType, String outputPath) {

		_deleteFile(
			StringBundler.concat(
				outputPath, "/service/", entity.getName(),
				_getSessionTypeName(sessionType), "ServiceUtil.java"));
	}

	private void _removeServiceWrapper(
		Entity entity, int sessionType, String outputPath) {

		_deleteFile(
			StringBundler.concat(
				outputPath, "/service/", entity.getName(),
				_getSessionTypeName(sessionType), "ServiceWrapper.java"));
	}

	private void _removeServletContextUtil(String outputPath) {
		_deleteFile(outputPath + "/service/ServletContextUtil.java");
	}

	@SuppressWarnings("unused")
	private void _removeUADAnonymizer(Entity entity) {
		_deleteFile(
			StringBundler.concat(
				entity.getUADOutputPath(), "/uad/anonymizer/", entity.getName(),
				"UADAnonymizer.java"));
	}

	@SuppressWarnings("unused")
	private void _removeUADAnonymizerTest(Entity entity) {
		_deleteFile(
			StringBundler.concat(
				entity.getUADTestIntegrationOutputPath(),
				"/uad/anonymizer/test/", entity.getName(),
				"UADAnonymizerTest.java"));
	}

	private void _removeUADDisplay(Entity entity) {
		_deleteFile(
			StringBundler.concat(
				entity.getUADOutputPath(), "/uad/display/", entity.getName(),
				"UADDisplay.java"));
	}

	private void _removeUADDisplayTest(Entity entity) {
		_deleteFile(
			StringBundler.concat(
				entity.getUADTestIntegrationOutputPath(), "/uad/display/test/",
				entity.getName(), "UADDisplayTest.java"));
	}

	@SuppressWarnings("unused")
	private void _removeUADExporter(Entity entity) {
		_deleteFile(
			StringBundler.concat(
				entity.getUADOutputPath(), "/uad/exporter/", entity.getName(),
				"UADExporter.java"));
	}

	@SuppressWarnings("unused")
	private void _removeUADExporterTest(Entity entity) {
		_deleteFile(
			StringBundler.concat(
				entity.getUADTestIntegrationOutputPath(), "/uad/exporter/test/",
				entity.getName(), "UADExporterTest.java"));
	}

	@SuppressWarnings("unused")
	private void _removeUADTestHelper(Entity entity) {
		_deleteFile(
			StringBundler.concat(
				entity.getUADTestIntegrationOutputPath(), "/uad/test/",
				entity.getName(), "UADTestHelper.java"));
	}

	private void _resolveEntity(Entity entity) throws Exception {
		if (entity.isResolved()) {
			return;
		}

		for (String referenceEntityNames :
				entity.getUnresolvedResolvedReferenceEntityNames()) {

			Entity referenceEntity = getEntity(referenceEntityNames);

			if (referenceEntity == null) {
				throw new ServiceBuilderException(
					StringBundler.concat(
						"Unable to resolve reference ", referenceEntityNames,
						" in ",
						ListUtil.toString(_entities, Entity.NAME_ACCESSOR)));
			}

			entity.addReferenceEntity(referenceEntity);
		}

		entity.setResolved();
	}

	private void _touch(File file) throws IOException {
		_mkdir(file.getParentFile());

		Files.createFile(file.toPath());
	}

	private void _write(File file, String s) throws Exception {
		Path path = file.toPath();

		Files.createDirectories(path.getParent());

		Files.write(path, s.getBytes(StandardCharsets.UTF_8));
	}

	private void _write(
			File file, String content, Set<String> modifiedFileNames)
		throws Exception {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");

		String year = simpleDateFormat.format(new Date());

		if (file.exists()) {
			String oldContent = _read(file);

			int x = oldContent.indexOf("/**\n * SPDX-FileCopyrightText: (c) ");

			if (x != -1) {
				year = oldContent.substring(x + 35, x + 39);
			}
		}

		String header = _read("copyright.txt");

		header = header.replaceFirst(Pattern.quote("{$year}"), year);

		content = header + "\n\n" + content;

		String fileName = _normalize(file.toString());

		int startIndex = 0;

		int index = fileName.indexOf("/java/");

		if (index > 0) {
			startIndex = index + 6;
		}
		else {
			index = fileName.indexOf("/src/");

			if (index > 0) {
				startIndex = index + 5;
			}
			else {

				// Older branches still have integration tests in portal-impl

				index = fileName.indexOf("/test/integration/");

				if (index > 0) {
					startIndex = index + 18;
				}
				else {
					throw new ServiceBuilderException(
						"Unable to parse package path from " + fileName);
				}
			}
		}

		ToolsUtil.writeFileRaw(
			file,
			JavaParser.parse(
				file,
				StringUtil.replace(
					fileName.substring(
						startIndex, fileName.lastIndexOf(StringPool.SLASH)),
					CharPool.SLASH, CharPool.PERIOD),
				content, _MAX_LINE_LENGTH, false),
			modifiedFileNames);
	}

	private static final int _DEFAULT_COLUMN_MAX_LENGTH = 75;

	private static final String[] _FORCE_UNIQUE_FINDER_PACKAGE_PATHS = {
		"com.liferay.counter", "com.liferay.portal"
	};

	private static final String _HIBERNATE_3_HBM_NAMESPACE =
		"\"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\"";

	private static final String _HIBERNATE_5_HBM_NAMESPACE =
		"\"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\"";

	private static final int _MAX_LINE_LENGTH = 80;

	private static final int _SESSION_TYPE_LOCAL = 1;

	private static final int _SESSION_TYPE_REMOTE = 0;

	private static final String _SPRING_NAMESPACE_BEANS = "beans";

	private static final String _SQL_CREATE_TABLE = "create table ";

	private static final String _TMP_DIR_NAME = System.getProperty(
		"java.io.tmpdir");

	private static final String _TPL_BASE_UAD_ANONYMIZER =
		ServiceBuilder._TPL_ROOT + "base_uad_anonymizer.ftl";

	private static final String _TPL_BASE_UAD_DISPLAY =
		ServiceBuilder._TPL_ROOT + "base_uad_display.ftl";

	private static final String _TPL_BASE_UAD_EXPORTER =
		ServiceBuilder._TPL_ROOT + "base_uad_exporter.ftl";

	private static final String _TPL_CT_SERVICE_IMPL =
		ServiceBuilder._TPL_ROOT + "ct_service_impl.ftl";

	private static final String _TPL_MODEL_TABLE =
		ServiceBuilder._TPL_ROOT + "model_table.ftl";

	private static final String _TPL_PERSISTENCE_CONSTANTS =
		ServiceBuilder._TPL_ROOT + "persistence_constants.ftl";

	private static final String _TPL_PERSISTENCE_TEST =
		ServiceBuilder._TPL_ROOT + "persistence_test.ftl";

	private static final String _TPL_ROOT =
		"com/liferay/portal/tools/service/builder/dependencies/";

	private static final String _TPL_SERVLET_CONTEXT_UTIL =
		_TPL_ROOT + "servlet_context_util.ftl";

	private static final String _TPL_UAD_ANOYMIZER =
		_TPL_ROOT + "uad_anonymizer.ftl";

	private static final String _TPL_UAD_BND = _TPL_ROOT + "uad_bnd.ftl";

	private static final String _TPL_UAD_CONSTANTS =
		_TPL_ROOT + "uad_constants.ftl";

	private static final String _TPL_UAD_DISPLAY =
		_TPL_ROOT + "uad_display.ftl";

	private static final String _TPL_UAD_EXPORTER =
		_TPL_ROOT + "uad_exporter.ftl";

	private static final Pattern _beansAttributePattern = Pattern.compile(
		"\\s+([^=]*)=\\s*\"([^\"]*)\"");
	private static final Pattern _beansPattern = Pattern.compile(
		"<beans[^>]*>");
	private static Configuration _configuration;
	private static final Pattern _dtdVersionPattern = Pattern.compile(
		".*service-builder_([^\\.]+)\\.dtd");
	private static final Pattern _getterPattern = Pattern.compile(
		StringBundler.concat(
			"public .* get.*", Pattern.quote("("), "|public boolean is.*",
			Pattern.quote("(")));
	private static final List<String> _highCardinalityColumnNames =
		Arrays.asList("externalReferenceCode", "uuid_");
	private static final Pattern _setterPattern = Pattern.compile(
		"public void set.*" + Pattern.quote("("));

	private String _apiDirName;
	private String _apiPackagePath;
	private String _author;
	private boolean _autoImportDefaultReferences;
	private boolean _autoNamespaceTables;
	private Set<String> _badAliasNames;
	private Set<String> _badColumnNames;
	private Set<String> _badTableNames;
	private String _beanLocatorUtil;
	private boolean _build;
	private long _buildNumber;
	private boolean _buildNumberIncrement;
	private boolean _changeTrackingEnabled;
	private Properties _compatProperties;
	private String _currentTplName;
	private int _databaseNameMaxLength = 30;
	private boolean _dependencyInjectorDS;
	private Version _dtdVersion;
	private List<Entity> _entities;
	private Map<String, EntityMapping> _entityMappings;
	private final Map<String, Entity> _entityPool = new HashMap<>();
	private String _hbmFileName;
	private String _implDirName;
	private String[] _incubationFeatures;
	private final Map<String, JavaClass> _javaClasses = new HashMap<>();
	private String _modelHintsFileName;
	private final Set<String> _modifiedFileNames = new HashSet<>();
	private boolean _mvccEnabled;
	private String _oldServiceOutputPath;
	private boolean _optimizeDBIndexes;
	private boolean _osgiModule;
	private String _outputPath;
	private String _packagePath;
	private String _pluginName;
	private String _portletShortName = StringPool.BLANK;
	private String _propsUtil;
	private String[] _readOnlyPrefixes;
	private Set<String> _resourceActionModels = new HashSet<>();
	private String _resourcesDirName;
	private String _serviceOutputPath;
	private boolean _shortNoSuchExceptionEnabled;
	private String _springFileName;
	private String[] _springNamespaces;
	private String _sqlDirName;
	private String _sqlFileName;
	private String _sqlIndexesFileName;
	private String _sqlSequencesFileName;
	private String _targetEntityName;
	private String _testDirName;
	private String _testOutputPath;
	private String _tplBadAliasNames = _TPL_ROOT + "bad_alias_names.txt";
	private String _tplBadColumnNames = _TPL_ROOT + "bad_column_names.txt";
	private String _tplBadTableNames = _TPL_ROOT + "bad_table_names.txt";
	private String _tplBlobModel = _TPL_ROOT + "blob_model.ftl";
	private String _tplEjbPK = _TPL_ROOT + "ejb_pk.ftl";
	private String _tplException = _TPL_ROOT + "exception.ftl";
	private String _tplExtendedModel = _TPL_ROOT + "extended_model.ftl";
	private String _tplExtendedModelBaseImpl =
		_TPL_ROOT + "extended_model_base_impl.ftl";
	private String _tplExtendedModelImpl =
		_TPL_ROOT + "extended_model_impl.ftl";
	private String _tplFinder = _TPL_ROOT + "finder.ftl";
	private String _tplFinderBaseImpl = _TPL_ROOT + "finder_base_impl.ftl";
	private String _tplFinderUtil = _TPL_ROOT + "finder_util.ftl";
	private String _tplHbmXml = _TPL_ROOT + "hbm_xml.ftl";
	private String _tplJsonJs = _TPL_ROOT + "json_js.ftl";
	private String _tplJsonJsMethod = _TPL_ROOT + "json_js_method.ftl";
	private String _tplModel = _TPL_ROOT + "model.ftl";
	private String _tplModelArgumentsResolver =
		_TPL_ROOT + "model_arguments_resolver.ftl";
	private String _tplModelCache = _TPL_ROOT + "model_cache.ftl";
	private String _tplModelHintsXml = _TPL_ROOT + "model_hints_xml.ftl";
	private String _tplModelImpl = _TPL_ROOT + "model_impl.ftl";
	private String _tplModelSoap = _TPL_ROOT + "model_soap.ftl";
	private String _tplModelWrapper = _TPL_ROOT + "model_wrapper.ftl";
	private String _tplPersistence = _TPL_ROOT + "persistence.ftl";
	private String _tplPersistenceImpl = _TPL_ROOT + "persistence_impl.ftl";
	private String _tplPersistenceUtil = _TPL_ROOT + "persistence_util.ftl";
	private String _tplProps = _TPL_ROOT + "props.ftl";
	private String _tplService = _TPL_ROOT + "service.ftl";
	private String _tplServiceBaseImpl = _TPL_ROOT + "service_base_impl.ftl";
	private String _tplServiceHttp = _TPL_ROOT + "service_http.ftl";
	private String _tplServiceImpl = _TPL_ROOT + "service_impl.ftl";
	private String _tplServicePropsUtil = _TPL_ROOT + "service_props_util.ftl";
	private String _tplServiceSoap = _TPL_ROOT + "service_soap.ftl";
	private String _tplServiceUtil = _TPL_ROOT + "service_util.ftl";
	private String _tplServiceWrapper = _TPL_ROOT + "service_wrapper.ftl";
	private String _tplSpringXml = _TPL_ROOT + "spring_xml.ftl";
	private final Map<String, List<Entity>> _uadApplicationEntities =
		new HashMap<>();
	private String _uadDirName;

}