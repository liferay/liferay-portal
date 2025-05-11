/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.resolver;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ContactLocalService;
import com.liferay.portal.kernel.service.ContactLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Digester;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.language.LanguageImpl;
import com.liferay.portal.model.impl.UserGroupImpl;
import com.liferay.portal.model.impl.UserImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.opensaml.integration.field.expression.handler.registry.UserFieldExpressionHandlerRegistry;
import com.liferay.saml.opensaml.integration.field.expression.resolver.UserFieldExpressionResolver;
import com.liferay.saml.opensaml.integration.field.expression.resolver.registry.UserFieldExpressionResolverRegistry;
import com.liferay.saml.opensaml.integration.internal.BaseSamlTestCase;
import com.liferay.saml.opensaml.integration.internal.field.expression.handler.DefaultUserFieldExpressionHandler;
import com.liferay.saml.opensaml.integration.internal.field.expression.handler.MembershipsUserFieldExpressionHandler;
import com.liferay.saml.opensaml.integration.internal.processor.factory.UserProcessorFactoryImpl;
import com.liferay.saml.opensaml.integration.internal.util.OpenSamlUtil;
import com.liferay.saml.opensaml.integration.resolver.UserResolver;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.service.SamlPeerBindingLocalService;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import com.liferay.saml.runtime.exception.SubjectException;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import org.mockito.stubbing.Answer;

import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLSubjectNameIdentifierContext;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;

/**
 * @author Mika Koivisto
 * @author Stian Sigvartsen
 */
public class DefaultUserResolverTest extends BaseSamlTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_mockDigesterUtil();
		_mockLanguageUtil();

		getMockPortalService(
			OrganizationLocalServiceUtil.class, OrganizationLocalService.class);

		_company = _mockCompany();
		_prefsProps = _mockPrefsProps();
		_samlProviderConfigurationHelper =
			_mockSamlProviderConfigurationHelper();
		_samlSpIdpConnection = _mockSamlSpIdConnection();

		_userGroupLocalService = _mockUserGroupLocalService();
		_userLocalService = _mockUserLocalService();

		_userFieldExpressionHandlerRegistry =
			_mockDefaultUserFieldExpressionRegistry(
				_createDefaultUserFieldExpressionHandler(
					_userLocalService, _prefsProps),
				_createMembershipsUserFieldExpressionHandler(
					_userGroupLocalService, _userLocalService));

		_testUserFieldExpressionResolver =
			new TestUserFieldExpressionResolver();

		_userFieldExpressionResolverRegistry =
			_mockUserFieldExpressionResolverRegistry(
				_testUserFieldExpressionResolver);

		ReflectionTestUtil.setFieldValue(
			_defaultUserResolver, "_companyLocalService",
			_mockCompanyLocalService(_company));
		ReflectionTestUtil.setFieldValue(
			_defaultUserResolver, "_samlPeerBindingLocalService",
			_mockSamlPeerBindingLocalService());
		ReflectionTestUtil.setFieldValue(
			_defaultUserResolver, "_samlProviderConfigurationHelper",
			_samlProviderConfigurationHelper);
		ReflectionTestUtil.setFieldValue(
			_defaultUserResolver, "_samlSpIdpConnectionLocalService",
			_mockSamlSpIdConnectionLocalService(_samlSpIdpConnection));
		ReflectionTestUtil.setFieldValue(
			_defaultUserResolver, "_userFieldExpressionHandlerRegistry",
			_userFieldExpressionHandlerRegistry);
		ReflectionTestUtil.setFieldValue(
			_defaultUserResolver, "_userFieldExpressionResolverRegistry",
			_userFieldExpressionResolverRegistry);
		ReflectionTestUtil.setFieldValue(
			_defaultUserResolver, "_userLocalService", _userLocalService);
		ReflectionTestUtil.setFieldValue(
			_defaultUserResolver, "_userProcessorFactory",
			new UserProcessorFactoryImpl());
	}

	@Test
	public void testImportUserWithAutoScreenName() throws Exception {
		Mockito.when(
			_company.isStrangers()
		).thenReturn(
			true
		);

		Mockito.when(
			_company.isStrangersWithMx()
		).thenReturn(
			true
		);

		Mockito.when(
			_prefsProps.getBoolean(
				Mockito.anyLong(),
				Mockito.eq(PropsKeys.USERS_SCREEN_NAME_ALWAYS_AUTOGENERATE))
		).thenReturn(
			true
		);

		_initMessageContext(
			false, NameIDType.UNSPECIFIED, _SAML_NAME_IDENTIFIER_VALUE);
		_initUnknownUserHandling(true);

		_testUserFieldExpressionResolver.setUserFieldExpression("screenName");

		User resolvedUser = _defaultUserResolver.resolveUser(
			new UserResolverSAMLContextImpl(_messageContext),
			new ServiceContext());

		Assert.assertNotNull(resolvedUser);

		Assert.assertEquals("NULL_PROVIDED", resolvedUser.getScreenName());
	}

	@Test
	public void testImportUserWithEmailAddress() throws Exception {
		Mockito.when(
			_company.isStrangers()
		).thenReturn(
			true
		);

		Mockito.when(
			_company.isStrangersWithMx()
		).thenReturn(
			true
		);

		_initMessageContext(
			true, NameIDType.EMAIL, _SUBJECT_NAME_IDENTIFIER_EMAIL_ADDRESS);
		_initUnknownUserHandling(false);

		_testUserFieldExpressionResolver.setUserFieldExpression("emailAddress");

		User resolvedUser = _defaultUserResolver.resolveUser(
			new UserResolverSAMLContextImpl(_messageContext),
			new ServiceContext());

		Assert.assertNotNull(resolvedUser);
	}

	@Test
	public void testImportUserWithScreenNameAttribute() throws Exception {
		Mockito.when(
			_company.isStrangers()
		).thenReturn(
			true
		);

		Mockito.when(
			_company.isStrangersWithMx()
		).thenReturn(
			true
		);

		_initMessageContext(
			true, NameIDType.UNSPECIFIED, _SAML_NAME_IDENTIFIER_VALUE);
		_initUnknownUserHandling(false);

		_testUserFieldExpressionResolver.setUserFieldExpression("screenName");

		User resolvedUser = _defaultUserResolver.resolveUser(
			new UserResolverSAMLContextImpl(_messageContext),
			new ServiceContext());

		Assert.assertNotNull(resolvedUser);
	}

	@Test
	public void testMatchingUserWithEmailAddressAttribute() throws Exception {
		Mockito.when(
			_company.isStrangers()
		).thenReturn(
			true
		);

		Mockito.when(
			_company.isStrangersWithMx()
		).thenReturn(
			true
		);

		_initMatchingUserHandling();
		_initMessageContext(
			true, NameIDType.EMAIL, _SAML_NAME_IDENTIFIER_VALUE);

		_testUserFieldExpressionResolver.setUserFieldExpression("emailAddress");

		User user = _defaultUserResolver.resolveUser(
			new UserResolverSAMLContextImpl(_messageContext),
			new ServiceContext());

		Assert.assertNotNull(user);
	}

	@Test
	public void testMatchingUserWithSAMLNameIDValue() throws Exception {
		Mockito.when(
			_company.isStrangers()
		).thenReturn(
			true
		);

		Mockito.when(
			_company.isStrangersWithMx()
		).thenReturn(
			true
		);

		_initMatchingUserHandling();
		_initMessageContext(
			false, NameIDType.UNSPECIFIED,
			_SUBJECT_NAME_IDENTIFIER_SCREEN_NAME);

		_testUserFieldExpressionResolver.setUserFieldExpression("screenName");

		User user = _defaultUserResolver.resolveUser(
			new UserResolverSAMLContextImpl(_messageContext),
			new ServiceContext());

		Assert.assertNotNull(user);
	}

	@Test
	public void testMatchingUserWithScreenNameAttribute() throws Exception {
		Mockito.when(
			_company.isStrangers()
		).thenReturn(
			true
		);

		Mockito.when(
			_company.isStrangersWithMx()
		).thenReturn(
			true
		);

		_initMatchingUserHandling();
		_initMessageContext(
			true, NameIDType.UNSPECIFIED, _SAML_NAME_IDENTIFIER_VALUE);

		_testUserFieldExpressionResolver.setUserFieldExpression("screenName");

		User user = _defaultUserResolver.resolveUser(
			new UserResolverSAMLContextImpl(_messageContext),
			new ServiceContext());

		Assert.assertNotNull(user);
	}

	@Test(expected = SubjectException.class)
	public void testStrangersNotAllowedToCreateAccounts() throws Exception {
		Mockito.when(
			_company.isStrangers()
		).thenReturn(
			false
		);

		_initMessageContext(
			true, NameIDType.EMAIL, _SUBJECT_NAME_IDENTIFIER_EMAIL_ADDRESS);
		_initUnknownUserHandling(false);

		_testUserFieldExpressionResolver.setUserFieldExpression("emailAddress");

		_defaultUserResolver.resolveUser(
			new UserResolverSAMLContextImpl(_messageContext),
			new ServiceContext());
	}

	@Test(expected = UserEmailAddressException.MustNotUseCompanyMx.class)
	public void testStrangersNotAllowedToCreateAccountsWithCompanyMx()
		throws Exception {

		Mockito.when(
			_company.isStrangers()
		).thenReturn(
			true
		);

		Mockito.when(
			_company.isStrangersWithMx()
		).thenReturn(
			false
		);

		_initMessageContext(
			true, NameIDType.EMAIL, _SUBJECT_NAME_IDENTIFIER_EMAIL_ADDRESS);
		_initUnknownUserHandling(false);

		_testUserFieldExpressionResolver.setUserFieldExpression("emailAddress");

		_defaultUserResolver.resolveUser(
			new UserResolverSAMLContextImpl(_messageContext),
			new ServiceContext());
	}

	@Test
	public void testUserGroupMapping() throws Exception {
		Mockito.when(
			_company.isStrangers()
		).thenReturn(
			true
		);

		Mockito.when(
			_company.isStrangersWithMx()
		).thenReturn(
			true
		);

		_initMessageContext(
			true, NameIDType.EMAIL, _SUBJECT_NAME_IDENTIFIER_EMAIL_ADDRESS);
		_initUnknownUserHandling(false);

		_attributes.add(
			OpenSamlUtil.buildAttribute(
				"membership:userGroups",
				new String[] {
					_USER_GROUP_NAME_EXISTING, _USER_GROUP_NAME_NEW
				}));

		_testUserFieldExpressionResolver.setUserFieldExpression("emailAddress");

		User user = _defaultUserResolver.resolveUser(
			new UserResolverSAMLContextImpl(_messageContext),
			new ServiceContext());

		Mockito.verify(
			_userGroupLocalService, Mockito.times(1)
		).addUserGroup(
			Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(),
			Mockito.eq(_USER_GROUP_NAME_NEW), Mockito.anyString(),
			Mockito.eq(null)
		);

		Mockito.verify(
			_userGroupLocalService, Mockito.times(1)
		).fetchUserGroup(
			Mockito.anyLong(), Mockito.eq(_USER_GROUP_NAME_EXISTING)
		);

		Assert.assertNotNull(user);

		List<UserGroup> userGroups = _userGroupLocalService.getUserUserGroups(
			user.getUserId());

		Assert.assertEquals(userGroups.toString(), 2, userGroups.size());

		UserGroup userGroup = userGroups.get(0);

		Assert.assertEquals(_USER_GROUP_NAME_EXISTING, userGroup.getName());

		userGroup = userGroups.get(1);

		Assert.assertEquals(_USER_GROUP_NAME_NEW, userGroup.getName());
	}

	private User _createBlankUser() {
		User user = new UserImpl();

		user.setNew(true);
		user.setPrimaryKey(0);

		return user;
	}

	private DefaultUserFieldExpressionHandler
		_createDefaultUserFieldExpressionHandler(
			UserLocalService userLocalService, PrefsProps prefsProps) {

		DefaultUserFieldExpressionHandler defaultUserFieldExpressionHandler =
			new DefaultUserFieldExpressionHandler();

		ReflectionTestUtil.setFieldValue(
			defaultUserFieldExpressionHandler, "_prefsProps", prefsProps);
		ReflectionTestUtil.setFieldValue(
			defaultUserFieldExpressionHandler, "_processingIndex", -1);
		ReflectionTestUtil.setFieldValue(
			defaultUserFieldExpressionHandler, "_userLocalService",
			userLocalService);

		return defaultUserFieldExpressionHandler;
	}

	private MembershipsUserFieldExpressionHandler
		_createMembershipsUserFieldExpressionHandler(
			UserGroupLocalService userGroupLocalService,
			UserLocalService userLocalService) {

		MembershipsUserFieldExpressionHandler
			membershipsUserFieldExpressionHandler =
				new MembershipsUserFieldExpressionHandler();

		ReflectionTestUtil.setFieldValue(
			membershipsUserFieldExpressionHandler, "_processingIndex", 100);
		ReflectionTestUtil.setFieldValue(
			membershipsUserFieldExpressionHandler, "_userGroupLocalService",
			userGroupLocalService);
		ReflectionTestUtil.setFieldValue(
			membershipsUserFieldExpressionHandler, "_userLocalService",
			userLocalService);

		return membershipsUserFieldExpressionHandler;
	}

	private void _initMatchingUserHandling() throws Exception {
		ContactLocalService contactLocalService = getMockPortalService(
			ContactLocalServiceUtil.class, ContactLocalService.class);

		Contact contact = Mockito.mock(Contact.class);

		Mockito.when(
			contact.getBirthday()
		).thenReturn(
			new Date()
		);

		Mockito.when(
			contactLocalService.getContact(Mockito.any(Long.class))
		).thenReturn(
			contact
		);

		User user = new UserImpl();

		user.setScreenName("test");
		user.setEmailAddress(_SUBJECT_NAME_IDENTIFIER_EMAIL_ADDRESS);
		user.setFirstName("test");
		user.setLastName("test");

		Mockito.when(
			user.getContact()
		).thenReturn(
			contact
		);

		Mockito.when(
			_userLocalService.addUser(
				Mockito.any(Long.class), Mockito.any(Long.class),
				Mockito.anyBoolean(), Mockito.nullable(String.class),
				Mockito.nullable(String.class), Mockito.anyBoolean(),
				Mockito.nullable(String.class), Mockito.nullable(String.class),
				Mockito.any(Locale.class), Mockito.nullable(String.class),
				Mockito.nullable(String.class), Mockito.nullable(String.class),
				Mockito.any(Integer.class), Mockito.any(Integer.class),
				Mockito.anyBoolean(), Mockito.any(Integer.class),
				Mockito.any(Integer.class), Mockito.any(Integer.class),
				Mockito.nullable(String.class), Mockito.any(Integer.class),
				Mockito.any(long[].class), Mockito.any(long[].class),
				Mockito.any(long[].class), Mockito.any(long[].class),
				Mockito.anyBoolean(), Mockito.any(ServiceContext.class))
		).thenReturn(
			null
		);

		Mockito.when(
			_userLocalService.getUserByEmailAddress(
				Mockito.any(Long.class),
				Mockito.eq(_SUBJECT_NAME_IDENTIFIER_EMAIL_ADDRESS))
		).thenReturn(
			user
		);

		Mockito.when(
			_userLocalService.getUserById(Mockito.any(Long.class))
		).thenReturn(
			user
		);

		Mockito.when(
			_userLocalService.getUserByScreenName(
				Mockito.any(Long.class),
				Mockito.eq(_SUBJECT_NAME_IDENTIFIER_SCREEN_NAME))
		).thenReturn(
			user
		);

		Mockito.when(
			_userLocalService.updateEmailAddress(
				Mockito.any(Long.class), Mockito.eq(StringPool.BLANK),
				Mockito.nullable(String.class), Mockito.nullable(String.class))
		).thenReturn(
			user
		);

		Mockito.when(
			_userLocalService.updateUser(
				Mockito.any(Long.class), Mockito.nullable(String.class),
				Mockito.nullable(String.class), Mockito.nullable(String.class),
				Mockito.anyBoolean(), Mockito.nullable(String.class),
				Mockito.nullable(String.class), Mockito.nullable(String.class),
				Mockito.nullable(String.class), Mockito.anyBoolean(),
				Mockito.any(byte[].class), Mockito.nullable(String.class),
				Mockito.nullable(String.class), Mockito.nullable(String.class),
				Mockito.nullable(String.class), Mockito.nullable(String.class),
				Mockito.nullable(String.class), Mockito.nullable(String.class),
				Mockito.any(Long.class), Mockito.any(Long.class),
				Mockito.anyBoolean(), Mockito.any(Integer.class),
				Mockito.any(Integer.class), Mockito.any(Integer.class),
				Mockito.nullable(String.class), Mockito.nullable(String.class),
				Mockito.nullable(String.class), Mockito.nullable(String.class),
				Mockito.nullable(String.class), Mockito.nullable(String.class),
				Mockito.any(long[].class), Mockito.any(long[].class),
				Mockito.any(long[].class), Mockito.anyList(),
				Mockito.any(long[].class), Mockito.any(ServiceContext.class))
		).thenReturn(
			user
		);
	}

	private void _initMessageContext(
		boolean addScreenNameAttribute, String nameIdFormat,
		String nameIdValue) {

		Assertion assertion = OpenSamlUtil.buildAssertion();

		NameID subjectNameID = OpenSamlUtil.buildNameId(
			nameIdFormat, null, "urn:liferay", nameIdValue);

		Subject subject = OpenSamlUtil.buildSubject(subjectNameID);

		SubjectConfirmation subjectConfirmation =
			OpenSamlUtil.buildSubjectConfirmation();

		subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);

		List<SubjectConfirmation> subjectConfirmations =
			subject.getSubjectConfirmations();

		subjectConfirmations.add(subjectConfirmation);

		assertion.setSubject(subject);

		List<AttributeStatement> attributeStatements =
			assertion.getAttributeStatements();

		AttributeStatement attributeStatement =
			OpenSamlUtil.buildAttributeStatement();

		attributeStatements.add(attributeStatement);

		_attributes = attributeStatement.getAttributes();

		_attributes.add(
			OpenSamlUtil.buildAttribute(
				"emailAddress", _SUBJECT_NAME_IDENTIFIER_EMAIL_ADDRESS));
		_attributes.add(OpenSamlUtil.buildAttribute("firstName", "test"));
		_attributes.add(OpenSamlUtil.buildAttribute("lastName", "test"));

		if (addScreenNameAttribute) {
			_attributes.add(
				OpenSamlUtil.buildAttribute(
					"screenName", _SUBJECT_NAME_IDENTIFIER_SCREEN_NAME));
		}

		_messageContext = new MessageContext<>();

		Response response = Mockito.mock(Response.class);

		Mockito.when(
			response.getAssertions()
		).thenReturn(
			Arrays.asList(assertion)
		);

		MessageContext<Response> inboundMessageContext = new MessageContext<>();

		inboundMessageContext.addSubcontext(
			new SubjectAssertionContext(assertion));
		inboundMessageContext.setMessage(response);

		InOutOperationContext<Response, Object> inOutOperationContext =
			new InOutOperationContext<>(
				inboundMessageContext, new MessageContext<>());

		_messageContext.addSubcontext(inOutOperationContext);

		SAMLSubjectNameIdentifierContext samlSubjectNameIdentifierContext =
			new SAMLSubjectNameIdentifierContext();

		samlSubjectNameIdentifierContext.setSubjectNameIdentifier(
			subjectNameID);

		_messageContext.addSubcontext(samlSubjectNameIdentifierContext);

		SAMLPeerEntityContext samlPeerEntityContext =
			_messageContext.getSubcontext(SAMLPeerEntityContext.class, true);

		samlPeerEntityContext.setEntityId(IDP_ENTITY_ID);
	}

	private void _initUnknownUserHandling(boolean autoScreenName)
		throws Exception {

		Mockito.when(
			_userLocalService.getUserByEmailAddress(
				1, _SUBJECT_NAME_IDENTIFIER_EMAIL_ADDRESS)
		).thenReturn(
			null
		);

		User user = Mockito.mock(User.class);

		Mockito.when(
			_userLocalService.addUser(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.nullable(String.class), Mockito.nullable(String.class),
				Mockito.anyBoolean(), Mockito.nullable(String.class),
				Mockito.eq(_SUBJECT_NAME_IDENTIFIER_EMAIL_ADDRESS),
				Mockito.any(Locale.class), Mockito.eq("test"),
				Mockito.nullable(String.class), Mockito.eq("test"),
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.nullable(String.class), Mockito.any(Integer.class),
				Mockito.nullable(long[].class), Mockito.nullable(long[].class),
				Mockito.nullable(long[].class), Mockito.nullable(long[].class),
				Mockito.eq(false), Mockito.any(ServiceContext.class))
		).thenAnswer(
			(Answer<User>)invocationOnMock -> {
				if (autoScreenName &&
					Validator.isBlank(invocationOnMock.getArgument(6))) {

					Mockito.when(
						user.getScreenName()
					).thenReturn(
						"NULL_PROVIDED"
					);

					return user;
				}
				else if (_SUBJECT_NAME_IDENTIFIER_SCREEN_NAME.equals(
							invocationOnMock.getArgument(6))) {

					return user;
				}

				return null;
			}
		);

		Mockito.when(
			_userLocalService.updateEmailAddressVerified(
				Mockito.any(Long.class), Mockito.eq(true))
		).thenReturn(
			user
		);

		Mockito.when(
			_userLocalService.updatePasswordReset(
				Mockito.any(Long.class), Mockito.eq(false))
		).thenReturn(
			user
		);
	}

	private Company _mockCompany() {
		Company company = Mockito.mock(Company.class);

		Mockito.when(
			company.hasCompanyMx(_SUBJECT_NAME_IDENTIFIER_EMAIL_ADDRESS)
		).thenReturn(
			true
		);

		return company;
	}

	private CompanyLocalService _mockCompanyLocalService(Company company)
		throws Exception {

		CompanyLocalService companyLocalService = Mockito.mock(
			CompanyLocalService.class);

		Mockito.when(
			companyLocalService.getCompany(Mockito.any(Long.class))
		).thenReturn(
			company
		);

		return companyLocalService;
	}

	private UserFieldExpressionHandlerRegistry
		_mockDefaultUserFieldExpressionRegistry(
			DefaultUserFieldExpressionHandler defaultUserFieldExpressionHandler,
			MembershipsUserFieldExpressionHandler
				membershipsUserFieldExpressionHandler) {

		UserFieldExpressionHandlerRegistry userFieldExpressionHandlerRegistry =
			Mockito.mock(UserFieldExpressionHandlerRegistry.class);

		Mockito.when(
			userFieldExpressionHandlerRegistry.getFieldExpressionHandler(
				Mockito.nullable(String.class))
		).thenReturn(
			defaultUserFieldExpressionHandler
		);

		Mockito.when(
			userFieldExpressionHandlerRegistry.getFieldExpressionHandler(
				Mockito.eq("membership"))
		).thenReturn(
			membershipsUserFieldExpressionHandler
		);

		Mockito.when(
			userFieldExpressionHandlerRegistry.
				getFieldExpressionHandlerPrefixes()
		).thenReturn(
			new HashSet<>(Sets.newSet("", "membership"))
		);

		return userFieldExpressionHandlerRegistry;
	}

	private void _mockDigesterUtil() {
		DigesterUtil digesterUtil = new DigesterUtil();

		Digester digester = Mockito.mock(Digester.class);

		Mockito.when(
			digester.digest(Mockito.nullable(String.class))
		).thenReturn(
			RandomTestUtil.randomString()
		);

		digesterUtil.setDigester(digester);
	}

	private void _mockLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(new LanguageImpl());
	}

	private PrefsProps _mockPrefsProps() {
		return Mockito.mock(PrefsProps.class);
	}

	private SamlPeerBindingLocalService _mockSamlPeerBindingLocalService() {
		SamlPeerBindingLocalService samlPeerBindingLocalService = Mockito.mock(
			SamlPeerBindingLocalService.class);

		Mockito.when(
			samlPeerBindingLocalService.fetchSamlPeerBinding(
				Mockito.any(Long.class), Mockito.any(boolean.class),
				Mockito.nullable(String.class), Mockito.nullable(String.class),
				Mockito.nullable(String.class), Mockito.nullable(String.class))
		).thenReturn(
			null
		);

		return samlPeerBindingLocalService;
	}

	private SamlProviderConfigurationHelper
		_mockSamlProviderConfigurationHelper() {

		SamlProviderConfigurationHelper samlProviderConfigurationHelper =
			Mockito.mock(SamlProviderConfigurationHelper.class);

		Mockito.when(
			samlProviderConfigurationHelper.isLDAPImportEnabled()
		).thenReturn(
			false
		);

		return samlProviderConfigurationHelper;
	}

	private SamlSpIdpConnection _mockSamlSpIdConnection() throws Exception {
		SamlSpIdpConnection samlSpIdpConnection = Mockito.mock(
			SamlSpIdpConnection.class);

		Mockito.when(
			samlSpIdpConnection.getNormalizedUserAttributeMappings()
		).thenReturn(
			PropertiesUtil.load(_ATTRIBUTE_MAPPINGS)
		);

		Mockito.when(
			samlSpIdpConnection.isUnknownUsersAreStrangers()
		).thenReturn(
			true
		);

		return samlSpIdpConnection;
	}

	private SamlSpIdpConnectionLocalService _mockSamlSpIdConnectionLocalService(
			SamlSpIdpConnection samlSpIdpConnection)
		throws Exception {

		SamlSpIdpConnectionLocalService samlSpIdpConnectionLocalService =
			Mockito.mock(SamlSpIdpConnectionLocalService.class);

		Mockito.when(
			samlSpIdpConnectionLocalService.getSamlSpIdpConnection(
				Mockito.any(Long.class), Mockito.nullable(String.class))
		).thenReturn(
			samlSpIdpConnection
		);

		return samlSpIdpConnectionLocalService;
	}

	private UserFieldExpressionResolverRegistry
		_mockUserFieldExpressionResolverRegistry(
			TestUserFieldExpressionResolver testUserFieldExpressionResolver) {

		UserFieldExpressionResolverRegistry
			userFieldExpressionResolverRegistry = Mockito.mock(
				UserFieldExpressionResolverRegistry.class);

		Mockito.when(
			userFieldExpressionResolverRegistry.getUserFieldExpressionResolver(
				Mockito.nullable(String.class))
		).thenReturn(
			testUserFieldExpressionResolver
		);

		return userFieldExpressionResolverRegistry;
	}

	private UserGroupLocalService _mockUserGroupLocalService()
		throws Exception {

		UserGroupLocalService userGroupLocalService = Mockito.mock(
			UserGroupLocalService.class);

		UserGroup existingUserGroup = new UserGroupImpl();

		existingUserGroup.setUserGroupId(1);
		existingUserGroup.setName(_USER_GROUP_NAME_EXISTING);

		Mockito.when(
			userGroupLocalService.fetchUserGroup(
				Mockito.anyLong(), Mockito.eq(existingUserGroup.getName()))
		).thenReturn(
			existingUserGroup
		);

		UserGroup newUserGroup = new UserGroupImpl();

		newUserGroup.setUserGroupId(2);
		newUserGroup.setName(_USER_GROUP_NAME_NEW);

		Mockito.when(
			userGroupLocalService.addUserGroup(
				Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.eq(newUserGroup.getName()), Mockito.anyString(),
				Mockito.eq(null))
		).thenReturn(
			newUserGroup
		);

		List<UserGroup> userGroups = new ArrayList<>();

		Mockito.when(
			userGroupLocalService.getUserUserGroups(Mockito.anyLong())
		).thenReturn(
			userGroups
		);

		Mockito.doAnswer(
			(Answer<Void>)invocationOnMock -> {
				userGroups.clear();

				for (long userGroupId :
						(long[])invocationOnMock.getArgument(1)) {

					if (userGroupId == existingUserGroup.getUserGroupId()) {
						userGroups.add(existingUserGroup);
					}
					else if (userGroupId == newUserGroup.getUserGroupId()) {
						userGroups.add(newUserGroup);
					}
				}

				return null;
			}
		).when(
			userGroupLocalService
		).setUserUserGroups(
			Mockito.anyLong(), Mockito.any(long[].class)
		);

		return userGroupLocalService;
	}

	private UserLocalService _mockUserLocalService() {
		UserLocalService userLocalService = Mockito.mock(
			UserLocalService.class);

		Mockito.when(
			userLocalService.createUser(Mockito.eq(0L))
		).thenReturn(
			_createBlankUser()
		);

		return userLocalService;
	}

	private static final String _ATTRIBUTE_MAPPINGS =
		"emailAddress=emailAddress\nfirstName=firstName\nlastName=lastName\n" +
			"screenName=screenName";

	private static final String _SAML_NAME_IDENTIFIER_VALUE = "testNameIdValue";

	private static final String _SUBJECT_NAME_IDENTIFIER_EMAIL_ADDRESS =
		"test@liferay.com";

	private static final String _SUBJECT_NAME_IDENTIFIER_SCREEN_NAME = "test";

	private static final String _USER_GROUP_NAME_EXISTING =
		RandomTestUtil.randomString();

	private static final String _USER_GROUP_NAME_NEW =
		RandomTestUtil.randomString();

	private List<Attribute> _attributes;
	private Company _company;
	private final DefaultUserResolver _defaultUserResolver =
		new DefaultUserResolver();
	private MessageContext<Response> _messageContext;
	private PrefsProps _prefsProps;
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;
	private SamlSpIdpConnection _samlSpIdpConnection;
	private TestUserFieldExpressionResolver _testUserFieldExpressionResolver;
	private UserFieldExpressionHandlerRegistry
		_userFieldExpressionHandlerRegistry;
	private UserFieldExpressionResolverRegistry
		_userFieldExpressionResolverRegistry;
	private UserGroupLocalService _userGroupLocalService;
	private UserLocalService _userLocalService;

	private static class TestUserFieldExpressionResolver
		implements UserFieldExpressionResolver {

		@Override
		public String getDescription(Locale locale) {
			return null;
		}

		@Override
		public String resolveUserFieldExpression(
				Map<String, List<Serializable>> incomingAttributeValues,
				UserResolver.UserResolverSAMLContext userResolverSAMLContext)
			throws Exception {

			return _userFieldExpression;
		}

		public void setUserFieldExpression(String userFieldExpression) {
			_userFieldExpression = userFieldExpression;
		}

		private String _userFieldExpression;

	}

}