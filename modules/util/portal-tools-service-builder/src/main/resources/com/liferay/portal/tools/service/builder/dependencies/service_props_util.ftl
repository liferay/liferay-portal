package ${servicePropsUtilPackagePath};

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.configuration.Filter;

import java.util.Properties;

/**
 * @author ${author}
 * @generated
 */
public class ${servicePropsUtilClassName} {

	<#if serviceBuilder.isVersionLTE_7_3_0()>
		public static void addProperties(Properties properties) {
			_instance._configuration.addProperties(properties);
		}
	</#if>

	public static boolean contains(String key) {
		return _instance._configuration.contains(key);
	}

	public static String get(String key) {
		return _instance._configuration.get(key);
	}

	public static String get(String key, Filter filter) {
		return _instance._configuration.get(key, filter);
	}

	public static String[] getArray(String key) {
		return _instance._configuration.getArray(key);
	}

	public static String[] getArray(String key, Filter filter) {
		return _instance._configuration.getArray(key, filter);
	}

	public static Properties getProperties() {
		return _instance._configuration.getProperties();
	}

	<#if serviceBuilder.isVersionLTE_7_3_0()>
		public static void removeProperties(Properties properties) {
			_instance._configuration.removeProperties(properties);
		}
	</#if>

	public static void set(String key, String value) {
		_instance._configuration.set(key, value);
	}

	private ${servicePropsUtilClassName}() {
		_configuration = ConfigurationFactoryUtil.getConfiguration(getClass().getClassLoader(), "service");
	}

	private static ${servicePropsUtilClassName} _instance = new ${servicePropsUtilClassName}();

	private Configuration _configuration;

}