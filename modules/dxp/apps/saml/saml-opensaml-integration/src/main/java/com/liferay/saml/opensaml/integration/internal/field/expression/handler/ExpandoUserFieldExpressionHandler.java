/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.field.expression.handler;

import com.liferay.expando.kernel.exception.ValueDataException;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.ldap.LDAPSettings;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ldap.SafeLdapContext;
import com.liferay.portal.security.ldap.SafeLdapFilter;
import com.liferay.portal.security.ldap.SafeLdapFilterConstraints;
import com.liferay.portal.security.ldap.SafeLdapFilterFactory;
import com.liferay.portal.security.ldap.SafeLdapNameFactory;
import com.liferay.portal.security.ldap.SafePortalLDAP;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.security.ldap.exportimport.LDAPUserImporter;
import com.liferay.portal.security.ldap.util.LDAPUtil;
import com.liferay.portal.security.ldap.validator.LDAPFilterException;
import com.liferay.portal.security.ldap.validator.LDAPFilterValidator;
import com.liferay.saml.opensaml.integration.field.expression.handler.UserFieldExpressionHandler;
import com.liferay.saml.opensaml.integration.processor.context.ProcessorContext;
import com.liferay.saml.opensaml.integration.processor.context.UserProcessorContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

import javax.naming.Binding;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Stian Sigvartsen
 */
@Component(
	property = {
		"display.index:Integer=100", "prefix=expando",
		"processing.index:Integer=100"
	},
	service = UserFieldExpressionHandler.class
)
public class ExpandoUserFieldExpressionHandler
	implements UserFieldExpressionHandler {

	@Override
	public void bindProcessorContext(
		UserProcessorContext userProcessorContext) {

		for (String validFieldExpression : getValidFieldExpressions()) {
			if (Validator.isBlank(
					userProcessorContext.getValue(
						String.class, validFieldExpression))) {

				continue;
			}

			ProcessorContext.Bind<ExpandoValue> userBind =
				userProcessorContext.bind(
					user -> _getExpandoValue(user, validFieldExpression),
					_processingIndex, validFieldExpression, this::_update);

			userBind.handleUnsafeStringArray(
				validFieldExpression,
				(expandoValue, values) -> {
					ExpandoColumn expandoColumn = expandoValue.getColumn();

					try {
						_setExpandoValueData(
							expandoValue,
							_valueConsumers.get(expandoColumn.getType()),
							values);
					}
					catch (Exception exception) {
						if (exception instanceof PortalException) {
							throw exception;
						}

						throw new PortalException(
							StringBundler.concat(
								"Unable to set value for expando column ",
								validFieldExpression, ": ",
								exception.getMessage()),
							exception);
					}
				});
		}
	}

	@Override
	public User getLdapUser(
			long companyId, String userIdentifier,
			String userIdentifierExpression)
		throws Exception {

		userIdentifier = _getNormalizedData(
			companyId, userIdentifierExpression, userIdentifier);

		Collection<LDAPServerConfiguration> ldapServerConfigurations =
			_ldapServerConfigurationProvider.getConfigurations(companyId);

		for (LDAPServerConfiguration ldapServerConfiguration :
				ldapServerConfigurations) {

			String providerUrl = ldapServerConfiguration.baseProviderURL();

			if (Validator.isNull(providerUrl)) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"No provider URL defined in " +
							ldapServerConfiguration);
				}

				continue;
			}

			User user = _getLdapUser(
				ldapServerConfiguration.ldapServerId(), companyId,
				userIdentifier, userIdentifierExpression);

			if (user != null) {
				return user;
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"User with the expando field ", userIdentifierExpression,
					"=", userIdentifier, " was not found in any LDAP servers"));
		}

		return null;
	}

	@Override
	public String getSectionLabel(Locale locale) {
		return ResourceBundleUtil.getString(
			ResourceBundleUtil.getBundle(
				locale, DefaultUserFieldExpressionHandler.class),
			"user-custom-fields");
	}

	@Override
	public User getUser(
			long companyId, String userIdentifier,
			String userIdentifierExpression)
		throws PortalException {

		if (userIdentifier == null) {
			return null;
		}

		List<ExpandoValue> expandoValues =
			_expandoValueLocalService.getColumnValues(
				companyId, User.class.getName(),
				ExpandoTableConstants.DEFAULT_TABLE_NAME,
				userIdentifierExpression,
				_getNormalizedData(
					companyId, userIdentifierExpression, userIdentifier),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		if (expandoValues.size() > 1) {
			List<Long> userIds = new ArrayList<>();

			expandoValues.forEach(
				expandoValue -> userIds.add(expandoValue.getClassPK()));

			throw new PortalException(
				StringBundler.concat(
					"User expando column \"", userIdentifierExpression,
					"\" and value \"", userIdentifier,
					"\" must match only 1 user, but it matched ", userIds));
		}

		for (ExpandoValue expandoValue : expandoValues) {
			User user = _userLocalService.fetchUserById(
				expandoValue.getClassPK());

			if (user != null) {
				return user;
			}
		}

		return null;
	}

	@Override
	public List<String> getValidFieldExpressions() {
		List<String> validExpressions = new ArrayList<>();

		Set<Integer> types = _valueConsumers.keySet();

		for (ExpandoColumn column :
				_expandoColumnLocalService.getDefaultTableColumns(
					CompanyThreadLocal.getCompanyId(), User.class.getName())) {

			if (types.contains(column.getType())) {
				validExpressions.add(column.getName());
			}
		}

		return Collections.unmodifiableList(validExpressions);
	}

	@Override
	public boolean isSupportedForUserMatching(String userIdentifier) {
		return true;
	}

	@FunctionalInterface
	public interface ValueConsumer<T> {

		public void accept(ExpandoValue expandoValue, T value)
			throws PortalException;

	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_processingIndex = GetterUtil.getInteger(
			properties.get("processing.index"));
	}

	private static <V> ValueConsumer<String[]> _getValueConsumer(
		Function<String[], V> function, ValueConsumer<V> valueConsumer) {

		return (expandoValue, value) -> valueConsumer.accept(
			expandoValue, function.apply(value));
	}

	private static <V> V _head(V[] values) {
		if (ArrayUtil.isEmpty(values)) {
			return null;
		}

		return values[0];
	}

	private ExpandoValue _getExpandoValue(
		User user, String validUserFieldExpression) {

		ExpandoValue expandoValue = null;

		if (!user.isNew()) {
			expandoValue = _expandoValueLocalService.fetchValue(
				user.getCompanyId(), User.class.getName(),
				ExpandoTableConstants.DEFAULT_TABLE_NAME,
				validUserFieldExpression, user.getUserId());
		}

		if (expandoValue == null) {
			ExpandoColumn column = null;

			ExpandoTable table = null;

			try {
				table = _expandoTableLocalService.getTable(
					user.getCompanyId(), User.class.getName(),
					ExpandoTableConstants.DEFAULT_TABLE_NAME);

				column = _expandoColumnLocalService.getColumn(
					table.getTableId(), validUserFieldExpression);
			}
			catch (PortalException portalException) {
				throw new SystemException(portalException);
			}

			expandoValue = _expandoValueLocalService.createExpandoValue(0);

			expandoValue.setCompanyId(user.getCompanyId());
			expandoValue.setColumnId(column.getColumnId());
			expandoValue.setClassName(User.class.getName());
			expandoValue.setClassPK(user.getUserId());
		}

		return expandoValue;
	}

	private User _getLdapUser(
			long ldapServerId, long companyId, String userIdentifier,
			String userIdentifierExpression)
		throws Exception {

		Properties userExpandoMappings = _ldapSettings.getUserExpandoMappings(
			ldapServerId, companyId);

		String attributeName = GetterUtil.getString(
			userExpandoMappings.getProperty(userIdentifierExpression));

		if (Validator.isBlank(attributeName)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"User expando field ", userIdentifierExpression,
						" is not mapped for LDAP server ", ldapServerId));
			}

			return null;
		}

		SafeLdapContext safeLdapContext = null;

		NamingEnumeration<SearchResult> enumeration = null;

		try {
			LDAPServerConfiguration ldapServerConfiguration =
				_ldapServerConfigurationProvider.getConfiguration(
					companyId, ldapServerId);

			safeLdapContext = _safePortalLDAP.getSafeLdapContext(
				ldapServerId, companyId);

			if (safeLdapContext == null) {
				_log.error("Unable to bind to the LDAP server");

				return null;
			}

			if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"LDAP server ID ", ldapServerId,
							" is no longer valid, company ", companyId,
							" now uses ",
							ldapServerConfiguration.ldapServerId()));
				}

				return null;
			}

			SafeLdapFilter safeLdapFilter = null;

			try {
				safeLdapFilter = SafeLdapFilterFactory.fromUnsafeFilter(
					ldapServerConfiguration.userSearchFilter(),
					_ldapFilterValidator);
			}
			catch (LDAPFilterException ldapFilterException) {
				throw new LDAPFilterException(
					"Invalid user search filter: ", ldapFilterException);
			}

			safeLdapFilter = safeLdapFilter.and(
				SafeLdapFilterConstraints.eq(attributeName, userIdentifier));

			Properties userMappings = _ldapSettings.getUserMappings(
				ldapServerId, companyId);

			String userMappingsScreenName = GetterUtil.getString(
				userMappings.getProperty("screenName"));

			userMappingsScreenName = StringUtil.toLowerCase(
				userMappingsScreenName);

			SearchControls searchControls = new SearchControls(
				SearchControls.SUBTREE_SCOPE, 1, 0,
				new String[] {userMappingsScreenName}, false, false);

			enumeration = safeLdapContext.search(
				LDAPUtil.getBaseDNSafeLdapName(ldapServerConfiguration),
				safeLdapFilter, searchControls);

			if (enumeration.hasMoreElements()) {
				if (_log.isDebugEnabled()) {
					_log.debug("Search filter returned at least one result");
				}

				Binding binding = enumeration.nextElement();

				Attributes attributes = _safePortalLDAP.getUserAttributes(
					ldapServerId, companyId, safeLdapContext,
					SafeLdapNameFactory.from(binding));

				return _ldapUserImporter.importUser(
					ldapServerId, companyId, safeLdapContext, attributes, null);
			}

			return null;
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Problem accessing LDAP server " + exception.getMessage());
			}

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new SystemException(
				"Problem accessing LDAP server " + exception.getMessage());
		}
		finally {
			if (enumeration != null) {
				enumeration.close();
			}

			if (safeLdapContext != null) {
				safeLdapContext.close();
			}
		}
	}

	private String _getNormalizedData(
			long companyId, String columnName, String... values)
		throws PortalException {

		ExpandoValue expandoValue =
			_expandoValueLocalService.createExpandoValue(0);

		ExpandoColumn expandoColumn = _expandoColumnLocalService.getColumn(
			companyId,
			_classNameLocalService.getClassNameId(User.class.getName()),
			ExpandoTableConstants.DEFAULT_TABLE_NAME, columnName);

		expandoValue.setColumnId(expandoColumn.getColumnId());

		_setExpandoValueData(
			expandoValue, _valueConsumers.get(expandoColumn.getType()), values);

		return expandoValue.getData();
	}

	private void _setExpandoValueData(
			ExpandoValue expandoValue, ValueConsumer<String[]> valueConsumer,
			String[] values)
		throws PortalException {

		if (valueConsumer == null) {
			ExpandoColumn expandoColumn = expandoValue.getColumn();

			throw new ValueDataException.UnsupportedColumnType(
				expandoColumn.getColumnId(),
				ExpandoColumnConstants.getTypeLabel(expandoColumn.getType()));
		}

		valueConsumer.accept(expandoValue, values);
	}

	private ExpandoValue _update(
			ExpandoValue currentExpandoValue, ExpandoValue newExpandoValue,
			ServiceContext serviceContext)
		throws PortalException {

		if (!newExpandoValue.isNew()) {
			return _expandoValueLocalService.updateExpandoValue(
				newExpandoValue);
		}

		ExpandoColumn column = _expandoColumnLocalService.getColumn(
			newExpandoValue.getColumnId());

		return _expandoValueLocalService.addValue(
			newExpandoValue.getCompanyId(), User.class.getName(),
			ExpandoTableConstants.DEFAULT_TABLE_NAME, column.getName(),
			newExpandoValue.getClassPK(), (Object)newExpandoValue.getData());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExpandoUserFieldExpressionHandler.class);

	private static final HashMap<Integer, ValueConsumer<String[]>>
		_valueConsumers = HashMapBuilder.<Integer, ValueConsumer<String[]>>put(
			ExpandoColumnConstants.BOOLEAN,
			_getValueConsumer(
				values -> GetterUtil.getBoolean(_head(values)),
				ExpandoValue::setBoolean)
		).put(
			ExpandoColumnConstants.BOOLEAN_ARRAY,
			_getValueConsumer(
				GetterUtil::getBooleanValues, ExpandoValue::setBooleanArray)
		).put(
			ExpandoColumnConstants.DOUBLE,
			_getValueConsumer(
				values -> GetterUtil.getDouble(_head(values)),
				ExpandoValue::setDouble)
		).put(
			ExpandoColumnConstants.DOUBLE_ARRAY,
			_getValueConsumer(
				GetterUtil::getDoubleValues, ExpandoValue::setDoubleArray)
		).put(
			ExpandoColumnConstants.FLOAT,
			_getValueConsumer(
				values -> GetterUtil.getFloat(_head(values)),
				ExpandoValue::setFloat)
		).put(
			ExpandoColumnConstants.FLOAT_ARRAY,
			_getValueConsumer(
				GetterUtil::getLongValues, ExpandoValue::setLongArray)
		).put(
			ExpandoColumnConstants.INTEGER,
			_getValueConsumer(
				values -> GetterUtil.getIntegerStrict(_head(values)),
				ExpandoValue::setInteger)
		).put(
			ExpandoColumnConstants.INTEGER_ARRAY,
			_getValueConsumer(
				values -> {
					if (values == null) {
						return null;
					}

					int[] valuesIntArray = new int[values.length];

					for (int i = 0; i < valuesIntArray.length; i++) {
						valuesIntArray[i] = GetterUtil.getIntegerStrict(
							values[i]);
					}

					return valuesIntArray;
				},
				ExpandoValue::setIntegerArray)
		).put(
			ExpandoColumnConstants.LONG,
			_getValueConsumer(
				values -> GetterUtil.getLongStrict(_head(values)),
				ExpandoValue::setLong)
		).put(
			ExpandoColumnConstants.LONG_ARRAY,
			_getValueConsumer(
				values -> {
					if (values == null) {
						return null;
					}

					long[] valuesLongArray = new long[values.length];

					for (int i = 0; i < valuesLongArray.length; i++) {
						valuesLongArray[i] = GetterUtil.getLongStrict(
							values[i]);
					}

					return valuesLongArray;
				},
				ExpandoValue::setLongArray)
		).put(
			ExpandoColumnConstants.NUMBER,
			_getValueConsumer(
				values -> GetterUtil.getNumber(_head(values)),
				ExpandoValue::setNumber)
		).put(
			ExpandoColumnConstants.NUMBER_ARRAY,
			_getValueConsumer(
				GetterUtil::getNumberValues, ExpandoValue::setNumberArray)
		).put(
			ExpandoColumnConstants.SHORT,
			_getValueConsumer(
				values -> GetterUtil.getShortStrict(_head(values)),
				ExpandoValue::setShort)
		).put(
			ExpandoColumnConstants.SHORT_ARRAY,
			_getValueConsumer(
				values -> {
					if (values == null) {
						return null;
					}

					short[] shortValues = new short[values.length];

					for (int i = 0; i < values.length; i++) {
						shortValues[i] = GetterUtil.getShortStrict(values[i]);
					}

					return shortValues;
				},
				ExpandoValue::setShortArray)
		).put(
			ExpandoColumnConstants.STRING,
			_getValueConsumer(
				ExpandoUserFieldExpressionHandler::_head,
				ExpandoValue::setString)
		).put(
			ExpandoColumnConstants.STRING_ARRAY,
			_getValueConsumer(Function.identity(), ExpandoValue::setStringArray)
		).build();

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

	@Reference
	private LDAPFilterValidator _ldapFilterValidator;

	@Reference(
		target = "(factoryPid=com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration)"
	)
	private ConfigurationProvider<LDAPServerConfiguration>
		_ldapServerConfigurationProvider;

	@Reference
	private LDAPSettings _ldapSettings;

	@Reference
	private LDAPUserImporter _ldapUserImporter;

	private int _processingIndex;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile SafePortalLDAP _safePortalLDAP;

	@Reference
	private UserLocalService _userLocalService;

}