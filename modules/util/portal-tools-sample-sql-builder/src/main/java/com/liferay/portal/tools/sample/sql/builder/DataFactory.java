/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryModel;
import com.liferay.account.model.AccountEntryUserRelModel;
import com.liferay.account.model.impl.AccountEntryModelImpl;
import com.liferay.account.model.impl.AccountEntryUserRelModelImpl;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetCategoryModel;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetEntryModel;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyModel;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.constants.AssetListEntryUsageConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryModel;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRelModel;
import com.liferay.asset.list.model.AssetListEntryUsageModel;
import com.liferay.asset.list.model.impl.AssetListEntryModelImpl;
import com.liferay.asset.list.model.impl.AssetListEntrySegmentsEntryRelModelImpl;
import com.liferay.asset.list.model.impl.AssetListEntryUsageModelImpl;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.model.BlogsEntryModel;
import com.liferay.blogs.model.impl.BlogsEntryModelImpl;
import com.liferay.blogs.social.BlogsActivityKeys;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceCurrencyModel;
import com.liferay.commerce.currency.model.impl.CommerceCurrencyModelImpl;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItemModel;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseModel;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseItemModelImpl;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseModelImpl;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItemModel;
import com.liferay.commerce.model.CommerceOrderModel;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.model.CommerceShippingMethodModel;
import com.liferay.commerce.model.impl.CommerceOrderItemModelImpl;
import com.liferay.commerce.model.impl.CommerceOrderModelImpl;
import com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl;
import com.liferay.commerce.price.list.model.CommercePriceEntryModel;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListModel;
import com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl;
import com.liferay.commerce.price.list.model.impl.CommercePriceListModelImpl;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CPAttachmentFileEntryModel;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLocalizationModel;
import com.liferay.commerce.product.model.CPDefinitionModel;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValueModel;
import com.liferay.commerce.product.model.CPInstanceModel;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CPOptionCategoryModel;
import com.liferay.commerce.product.model.CPOptionModel;
import com.liferay.commerce.product.model.CPOptionValueModel;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.model.CPSpecificationOptionModel;
import com.liferay.commerce.product.model.CPTaxCategoryModel;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CProductModel;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceCatalogModel;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelModel;
import com.liferay.commerce.product.model.CommerceChannelRelModel;
import com.liferay.commerce.product.model.impl.CPAttachmentFileEntryModelImpl;
import com.liferay.commerce.product.model.impl.CPDefinitionLocalizationModelImpl;
import com.liferay.commerce.product.model.impl.CPDefinitionModelImpl;
import com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl;
import com.liferay.commerce.product.model.impl.CPInstanceModelImpl;
import com.liferay.commerce.product.model.impl.CPOptionCategoryModelImpl;
import com.liferay.commerce.product.model.impl.CPOptionModelImpl;
import com.liferay.commerce.product.model.impl.CPOptionValueModelImpl;
import com.liferay.commerce.product.model.impl.CPSpecificationOptionModelImpl;
import com.liferay.commerce.product.model.impl.CPTaxCategoryModelImpl;
import com.liferay.commerce.product.model.impl.CProductModelImpl;
import com.liferay.commerce.product.model.impl.CommerceCatalogModelImpl;
import com.liferay.commerce.product.model.impl.CommerceChannelModelImpl;
import com.liferay.commerce.product.model.impl.CommerceChannelRelModelImpl;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionModel;
import com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionModelImpl;
import com.liferay.counter.kernel.model.Counter;
import com.liferay.counter.kernel.model.CounterModel;
import com.liferay.counter.model.impl.CounterModelImpl;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryMetadataModel;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileEntryTypeModel;
import com.liferay.document.library.kernel.model.DLFileVersionModel;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderModel;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.constants.DDLRecordConstants;
import com.liferay.dynamic.data.lists.constants.DDLRecordSetConstants;
import com.liferay.dynamic.data.lists.model.DDLRecordModel;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordSetModel;
import com.liferay.dynamic.data.lists.model.DDLRecordVersionModel;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordModelImpl;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordSetModelImpl;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.dynamic.data.mapping.model.DDMField;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttributeModel;
import com.liferay.dynamic.data.mapping.model.DDMFieldModel;
import com.liferay.dynamic.data.mapping.model.DDMStorageLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayoutModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersionModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateVersionModel;
import com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStorageLinkModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureLinkModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureVersionModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateLinkModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateVersionModelImpl;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollectionModel;
import com.liferay.fragment.model.FragmentEntryLinkModel;
import com.liferay.fragment.model.FragmentEntryModel;
import com.liferay.fragment.model.impl.FragmentCollectionModelImpl;
import com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl;
import com.liferay.fragment.model.impl.FragmentEntryModelImpl;
import com.liferay.friendly.url.internal.util.FriendlyURLNormalizerImpl;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalizationModel;
import com.liferay.friendly.url.model.FriendlyURLEntryMappingModel;
import com.liferay.friendly.url.model.FriendlyURLEntryModel;
import com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl;
import com.liferay.friendly.url.model.impl.FriendlyURLEntryMappingModelImpl;
import com.liferay.friendly.url.model.impl.FriendlyURLEntryModelImpl;
import com.liferay.journal.constants.JournalActivityKeys;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.model.JournalArticleLocalizationModel;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.journal.model.JournalArticleResourceModel;
import com.liferay.journal.model.impl.JournalArticleLocalizationModelImpl;
import com.liferay.journal.model.impl.JournalArticleModelImpl;
import com.liferay.journal.model.impl.JournalArticleResourceModelImpl;
import com.liferay.layout.model.LayoutClassedModelUsageModel;
import com.liferay.layout.model.impl.LayoutClassedModelUsageModelImpl;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureModel;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelModel;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureModelImpl;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelModelImpl;
import com.liferay.layout.util.constants.LayoutClassedModelUsageConstants;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeDefinitionModel;
import com.liferay.list.type.model.ListTypeEntryModel;
import com.liferay.list.type.model.impl.ListTypeDefinitionModelImpl;
import com.liferay.list.type.model.impl.ListTypeEntryModelImpl;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBCategoryModel;
import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.message.boards.model.MBDiscussionModel;
import com.liferay.message.boards.model.MBMailingListModel;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageModel;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.model.MBThreadFlagModel;
import com.liferay.message.boards.model.MBThreadModel;
import com.liferay.message.boards.model.impl.MBCategoryModelImpl;
import com.liferay.message.boards.model.impl.MBDiscussionModelImpl;
import com.liferay.message.boards.model.impl.MBMailingListModelImpl;
import com.liferay.message.boards.model.impl.MBMessageModelImpl;
import com.liferay.message.boards.model.impl.MBThreadFlagModelImpl;
import com.liferay.message.boards.model.impl.MBThreadModelImpl;
import com.liferay.message.boards.social.MBActivityKeys;
import com.liferay.normalizer.Normalizer;
import com.liferay.notification.constants.NotificationTemplateConstants;
import com.liferay.notification.model.NotificationTemplateModel;
import com.liferay.notification.model.impl.NotificationTemplateModelImpl;
import com.liferay.object.constants.ObjectActionConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectActionModel;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionModel;
import com.liferay.object.model.ObjectEntryModel;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldModel;
import com.liferay.object.model.ObjectFieldSettingModel;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectFolderModel;
import com.liferay.object.model.ObjectRelationshipModel;
import com.liferay.object.model.ObjectStateFlowModel;
import com.liferay.object.model.ObjectStateModel;
import com.liferay.object.model.ObjectStateTransitionModel;
import com.liferay.object.model.impl.ObjectActionModelImpl;
import com.liferay.object.model.impl.ObjectDefinitionImpl;
import com.liferay.object.model.impl.ObjectEntryModelImpl;
import com.liferay.object.model.impl.ObjectFieldImpl;
import com.liferay.object.model.impl.ObjectFieldSettingModelImpl;
import com.liferay.object.model.impl.ObjectFolderModelImpl;
import com.liferay.object.model.impl.ObjectRelationshipModelImpl;
import com.liferay.object.model.impl.ObjectStateFlowModelImpl;
import com.liferay.object.model.impl.ObjectStateModelImpl;
import com.liferay.object.model.impl.ObjectStateTransitionModelImpl;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTable;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.metadata.RawMetadataProcessor;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.AddressModel;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyModel;
import com.liferay.portal.kernel.model.ContactConstants;
import com.liferay.portal.kernel.model.ContactModel;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.CountryModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutFriendlyURLModel;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutPrototypeModel;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetModel;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.PortalPreferencesModel;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferenceValue;
import com.liferay.portal.kernel.model.PortletPreferenceValueModel;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.model.ReleaseModel;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.ResourcePermissionModel;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.model.UserPersonalSite;
import com.liferay.portal.kernel.model.VirtualHostModel;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.theme.NavItem;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.impl.AddressModelImpl;
import com.liferay.portal.model.impl.ClassNameModelImpl;
import com.liferay.portal.model.impl.CompanyModelImpl;
import com.liferay.portal.model.impl.ContactModelImpl;
import com.liferay.portal.model.impl.CountryModelImpl;
import com.liferay.portal.model.impl.GroupModelImpl;
import com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl;
import com.liferay.portal.model.impl.LayoutModelImpl;
import com.liferay.portal.model.impl.LayoutPrototypeModelImpl;
import com.liferay.portal.model.impl.LayoutSetModelImpl;
import com.liferay.portal.model.impl.PortalPreferencesModelImpl;
import com.liferay.portal.model.impl.PortletPreferenceValueImpl;
import com.liferay.portal.model.impl.PortletPreferenceValueModelImpl;
import com.liferay.portal.model.impl.PortletPreferencesModelImpl;
import com.liferay.portal.model.impl.ReleaseModelImpl;
import com.liferay.portal.model.impl.ResourcePermissionModelImpl;
import com.liferay.portal.model.impl.RoleModelImpl;
import com.liferay.portal.model.impl.UserModelImpl;
import com.liferay.portal.model.impl.VirtualHostModelImpl;
import com.liferay.portal.search.web.constants.SearchBarPortletKeys;
import com.liferay.portal.search.web.constants.SearchResultsPortletKeys;
import com.liferay.portal.search.web.internal.category.facet.constants.CategoryFacetPortletKeys;
import com.liferay.portal.search.web.internal.folder.facet.constants.FolderFacetPortletKeys;
import com.liferay.portal.search.web.internal.modified.facet.constants.ModifiedFacetPortletKeys;
import com.liferay.portal.search.web.internal.search.options.constants.SearchOptionsPortletKeys;
import com.liferay.portal.search.web.internal.site.facet.constants.SiteFacetPortletKeys;
import com.liferay.portal.search.web.internal.suggestions.constants.SuggestionsPortletKeys;
import com.liferay.portal.search.web.internal.tag.facet.constants.TagFacetPortletKeys;
import com.liferay.portal.search.web.internal.type.facet.constants.TypeFacetPortletKeys;
import com.liferay.portal.search.web.internal.user.facet.constants.UserFacetPortletKeys;
import com.liferay.portal.service.impl.LayoutLocalServiceImpl;
import com.liferay.portal.upgrade.PortalUpgradeProcess;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.PortletPreferencesFactoryImpl;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl;
import com.liferay.portlet.asset.model.impl.AssetEntryModelImpl;
import com.liferay.portlet.asset.model.impl.AssetTagModelImpl;
import com.liferay.portlet.asset.model.impl.AssetVocabularyModelImpl;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryMetadataModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryTypeModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileVersionModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl;
import com.liferay.portlet.documentlibrary.social.DLActivityKeys;
import com.liferay.portlet.social.model.impl.SocialActivityModelImpl;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsEntryModel;
import com.liferay.segments.model.SegmentsExperienceModel;
import com.liferay.segments.model.impl.SegmentsEntryModelImpl;
import com.liferay.segments.model.impl.SegmentsExperienceModelImpl;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.model.SocialActivityModel;
import com.liferay.subscription.constants.SubscriptionConstants;
import com.liferay.subscription.model.SubscriptionModel;
import com.liferay.subscription.model.impl.SubscriptionModelImpl;
import com.liferay.util.SimpleCounter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.math.BigDecimal;

import java.sql.Types;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

/**
 * @author Brian Wing Shun Chan
 */
public class DataFactory {

	public DataFactory() throws Exception {
		_simpleDateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", TimeZone.getDefault());

		int totalCompanyCount = BenchmarksPropsValues.MAX_COMPANY_COUNT + 1;

		int groupCount =
			BenchmarksPropsValues.MAX_GROUP_COUNT +
				BenchmarksPropsValues.MAX_COMMERCE_GROUP_COUNT;

		int totalGroupCount = groupCount * totalCompanyCount;

		_counter = new SimpleCounter(totalGroupCount + 1);

		_dlFileEntryIdCounter = new SimpleCounter();
		_futureDateCounter = new SimpleCounter();
		_groupCounter = new SimpleCounter(1);
		_layoutPlidCounter = new SimpleCounter();
		_layoutSetIdCounter = new SimpleCounter();
		_portletPreferenceValueIdCounter = new SimpleCounter();
		_resourcePermissionIdCounter = new SimpleCounter();
		_segmentsExperienceCounter = new SimpleCounter();
		_socialActivityIdCounter = new SimpleCounter();
		_timeCounter = new SimpleCounter();
		_userScreenNameCounter = new SimpleCounter();

		List<String> models = ModelHintsUtil.getModels();

		models.add(
			DDMTemplate.class.getName() + StringPool.DASH +
				JournalArticle.class.getName());
		models.add(JournalArticleDisplay.class.getName());
		models.add(Layout.class.getName());
		models.add(NavItem.class.getName());
		models.add(PortletDisplayTemplate.class.getName());
		models.add(UserPersonalSite.class.getName());

		for (String model : models) {
			ClassNameModel classNameModel = new ClassNameModelImpl();

			classNameModel.setClassNameId(_counter.get());
			classNameModel.setValue(model);

			_classNameModels.put(model, classNameModel);
		}

		_assetClassNameIds = new long[] {
			getClassNameId(BlogsEntry.class),
			getClassNameId(JournalArticle.class)
		};

		_dlDDMStructureContent = _readFile(
			"ddm_structure/ddm_structure_basic_document.json");
		_dlDDMStructureLayoutContent = _readFile(
			"ddm_structure/ddm_structure_layout_basic_document.json");
		_journalDDMStructureContent = _readFile(
			"ddm_structure/ddm_structure_basic_web_content.json");
		_journalDDMStructureLayoutContent = _readFile(
			"ddm_structure/ddm_structure_layout_basic_web_content.json");
		_layoutPageTemplateStructureRelData = _readFile(
			"layout_page_template_structure_rel.json");

		_defaultAssetPublisherPortletPreferencesImpl =
			(PortletPreferencesImpl)_portletPreferencesFactory.fromDefaultXML(
				_readFile("default_asset_publisher_preference.xml"));

		initJournalArticleContent();

		_firstNames = _readLines("user_name/first_names.txt");
		_lastNames = _readLines("user_name/last_names.txt");

		_friendlyURLNormalizer = new FriendlyURLNormalizerImpl();

		Field field = ReflectionUtil.getDeclaredField(
			FriendlyURLNormalizerImpl.class, "_normalizer");

		field.set(_friendlyURLNormalizer, (Normalizer)s -> s);
	}

	public List<String> generateDynamicSQLs(
		String dbTableName, long dlFileEntryId, long objectEntryId,
		List<ObjectFieldModel> objectFieldModels, long relatedObjectEntryId) {

		StringBundler sb = new StringBundler(
			5 + (3 * objectFieldModels.size()));

		sb.append("insert into ");
		sb.append(dbTableName);
		sb.append(" values (");
		sb.append(objectEntryId);

		for (ObjectFieldModel objectFieldModel : objectFieldModels) {
			if (objectFieldModel.getSystem()) {
				continue;
			}

			Object value = objectFieldModel.getName() + objectEntryId;

			if (StringUtil.equals(
					objectFieldModel.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

				value = dlFileEntryId;
			}
			else if (StringUtil.equals(
						objectFieldModel.getBusinessType(),
						ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

				value = _defaultListTypeEntryKey;
			}
			else if (StringUtil.equals(
						objectFieldModel.getBusinessType(),
						ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

				value = (relatedObjectEntryId > 0) ? relatedObjectEntryId : 0;
			}

			sb.append(", '");
			sb.append(value);
			sb.append("'");
		}

		sb.append(");");

		return ListUtil.fromArray(
			sb.toString(),
			StringBundler.concat(
				"insert into ", dbTableName, "_x values (", objectEntryId,
				");"));
	}

	public RoleModel getAdministratorRoleModel() {
		return _administratorRoleModel;
	}

	public List<Long> getAssetCategoryIds(AssetEntryModel assetEntryModel) {
		Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap =
			_assetCategoryModelsMaps[(int)assetEntryModel.getGroupId() - 1];

		if ((assetCategoryModelsMap == null) ||
			assetCategoryModelsMap.isEmpty()) {

			return Collections.emptyList();
		}

		List<AssetCategoryModel> assetCategoryModels =
			assetCategoryModelsMap.get(assetEntryModel.getClassNameId());

		if (ListUtil.isEmpty(assetCategoryModels)) {
			return Collections.emptyList();
		}

		if (_assetCategoryCounters == null) {
			_assetCategoryCounters =
				(Map<Long, SimpleCounter>[])new HashMap<?, ?>
					[(BenchmarksPropsValues.MAX_COMPANY_COUNT + 1) *
						BenchmarksPropsValues.MAX_GROUP_COUNT];
		}

		SimpleCounter counter = getSimpleCounter(
			_assetCategoryCounters, assetEntryModel.getGroupId(),
			assetEntryModel.getClassNameId());

		List<Long> assetCategoryIds = new ArrayList<>(
			BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_CATEGORY_COUNT);

		for (int i = 0;
			 i < BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_CATEGORY_COUNT;
			 i++) {

			int index = (int)counter.get() % assetCategoryModels.size();

			AssetCategoryModel assetCategoryModel = assetCategoryModels.get(
				index);

			assetCategoryIds.add(assetCategoryModel.getCategoryId());
		}

		return assetCategoryIds;
	}

	public List<Long> getAssetTagIds(AssetEntryModel assetEntryModel) {
		Map<Long, List<AssetTagModel>> assetTagModelsMap =
			_assetTagModelsMaps[(int)assetEntryModel.getGroupId() - 1];

		if ((assetTagModelsMap == null) || assetTagModelsMap.isEmpty()) {
			return Collections.emptyList();
		}

		List<AssetTagModel> assetTagModels = assetTagModelsMap.get(
			assetEntryModel.getClassNameId());

		if (ListUtil.isEmpty(assetTagModels)) {
			return Collections.emptyList();
		}

		if (_assetTagCounters == null) {
			_assetTagCounters = (Map<Long, SimpleCounter>[])new HashMap<?, ?>
				[(BenchmarksPropsValues.MAX_COMPANY_COUNT + 1) *
					BenchmarksPropsValues.MAX_GROUP_COUNT];
		}

		SimpleCounter counter = getSimpleCounter(
			_assetTagCounters, assetEntryModel.getGroupId(),
			assetEntryModel.getClassNameId());

		List<Long> assetTagIds = new ArrayList<>(
			BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_TAG_COUNT);

		for (int i = 0;
			 i < BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_TAG_COUNT;
			 i++) {

			int index = (int)counter.get() % assetTagModels.size();

			AssetTagModel assetTagModel = assetTagModels.get(index);

			assetTagIds.add(assetTagModel.getTagId());
		}

		return assetTagIds;
	}

	public long getBlogsEntryClassNameId() {
		return getClassNameId(BlogsEntry.class);
	}

	public long getClassNameId(Class<?> clazz) {
		return getClassNameId(clazz.getName());
	}

	public long getClassNameId(String className) {
		ClassNameModel classNameModel = _classNameModels.get(className);

		return classNameModel.getClassNameId();
	}

	public Collection<ClassNameModel> getClassNameModels() {
		return _classNameModels.values();
	}

	public long getCommerceInventoryWarehouseClassNameId() {
		return getClassNameId(CommerceInventoryWarehouse.class);
	}

	public long getCounterNext() {
		return _counter.get();
	}

	public long getCPDefinitionClassNameId() {
		return getClassNameId(CPDefinition.class);
	}

	public long getCPInstanceId(long cpDefinitionId) {
		CPInstanceModel cpInstanceModel = _cpInstanceModels.get(cpDefinitionId);

		return cpInstanceModel.getCPInstanceId();
	}

	public long getCProductClassNameId() {
		return getClassNameId(CProduct.class);
	}

	public long getDefaultDLDDMStructureId() {
		return _defaultDLDDMStructureId;
	}

	public long getDLFileEntryClassNameId() {
		return getClassNameId(DLFileEntry.class);
	}

	public String getDynamicObjectDefinitionTableCreateSQL(
		ObjectDefinitionModel objectDefinitionModel,
		List<ObjectFieldModel> objectFieldModels) {

		if (!_objectDefinitionDBTableNames.add(
				objectDefinitionModel.getDBTableName())) {

			return StringPool.BLANK;
		}

		List<ObjectField> objectFields = new ArrayList<>();

		for (ObjectFieldModel objectFieldModel : objectFieldModels) {
			if (objectFieldModel.getSystem()) {
				continue;
			}

			objectFields.add((ObjectField)objectFieldModel);
		}

		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			new DynamicObjectDefinitionTable(
				(ObjectDefinition)objectDefinitionModel, objectFields,
				objectDefinitionModel.getDBTableName());

		return dynamicObjectDefinitionTable.getCreateTableSQL();
	}

	public String getExtensionDynamicObjectDefinitionTableCreateSQL(
		ObjectDefinitionModel objectDefinitionModel) {

		ObjectDefinition objectDefinition =
			(ObjectDefinition)objectDefinitionModel;

		if (!_objectDefinitionDBTableNames.add(
				objectDefinition.getExtensionDBTableName())) {

			return StringPool.BLANK;
		}

		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			new DynamicObjectDefinitionTable(
				objectDefinition, new ArrayList<>(),
				objectDefinition.getExtensionDBTableName());

		return dynamicObjectDefinitionTable.getCreateTableSQL();
	}

	public RoleModel getGuestRoleModel() {
		return _guestRoleModel;
	}

	public long getJournalArticleClassNameId() {
		return getClassNameId(JournalArticle.class);
	}

	public String getJournalArticleLayoutColumn(String portletPrefix) {
		StringBundler sb = new StringBundler(
			3 * BenchmarksPropsValues.MAX_JOURNAL_ARTICLE_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_JOURNAL_ARTICLE_COUNT;
			 i++) {

			sb.append(portletPrefix);
			sb.append(i);
			sb.append(StringPool.COMMA);
		}

		return sb.toString();
	}

	public int getMaxAccountEntryCommerceOrderCount() {
		return BenchmarksPropsValues.MAX_ACCOUNT_ENTRY_COMMERCE_ORDER_COUNT;
	}

	public int getMaxAssetPublisherPageCount() {
		return BenchmarksPropsValues.MAX_ASSETPUBLISHER_PAGE_COUNT;
	}

	public int getMaxBlogsEntryCommentCount() {
		return BenchmarksPropsValues.MAX_BLOGS_ENTRY_COMMENT_COUNT;
	}

	public int getMaxCommerceGroupCount() {
		return BenchmarksPropsValues.MAX_COMMERCE_GROUP_COUNT;
	}

	public int getMaxContentLayoutCount() {
		return BenchmarksPropsValues.MAX_CONTENT_LAYOUT_COUNT;
	}

	public int getMaxCPDefinitionAttachmentTypeImageCount() {
		return BenchmarksPropsValues.
			MAX_CP_DEFINITION_ATTACHMENT_TYPE_IMAGE_COUNT;
	}

	public int getMaxCPDefinitionAttachmentTypePDFCount() {
		return BenchmarksPropsValues.
			MAX_CP_DEFINITION_ATTACHMENT_TYPE_PDF_COUNT;
	}

	public int getMaxCPDefinitionSpecificationOptionValueCount() {
		return BenchmarksPropsValues.
			MAX_CP_DEFINITION_SPECIFICATION_OPTION_VALUE_COUNT;
	}

	public int getMaxDDLRecordCount() {
		return BenchmarksPropsValues.MAX_DDL_RECORD_COUNT;
	}

	public int getMaxDDLRecordSetCount() {
		return BenchmarksPropsValues.MAX_DDL_RECORD_SET_COUNT;
	}

	public int getMaxDLFolderDepth() {
		return BenchmarksPropsValues.MAX_DL_FOLDER_DEPTH;
	}

	public int getMaxFragmentEntryLinkCount() {
		return BenchmarksPropsValues.MAX_FRAGMENT_ENTRY_LINK_COUNT;
	}

	public int getMaxGroupCount() {
		return BenchmarksPropsValues.MAX_GROUP_COUNT;
	}

	public int getMaxJournalArticleCount() {
		return BenchmarksPropsValues.MAX_JOURNAL_ARTICLE_COUNT;
	}

	public int getMaxJournalArticlePageCount() {
		return BenchmarksPropsValues.MAX_JOURNAL_ARTICLE_PAGE_COUNT;
	}

	public int getMaxJournalArticleVersionCount() {
		return BenchmarksPropsValues.MAX_JOURNAL_ARTICLE_VERSION_COUNT;
	}

	public int getMaxObjectEntryPageCount() {
		return BenchmarksPropsValues.MAX_OBJECT_ENTRY_PAGE_COUNT;
	}

	public int getMaxSegmentsEntrySegmentsExperienceCount() {
		return BenchmarksPropsValues.
			MAX_SEGMENTS_ENTRY_SEGMENTS_EXPERIENCE_COUNT;
	}

	public List<Long> getNewUserGroupIds(
		long groupId, GroupModel guestGroupModel) {

		List<Long> groupIds = new ArrayList<>(
			BenchmarksPropsValues.MAX_USER_TO_GROUP_COUNT + 1);

		groupIds.add(guestGroupModel.getGroupId());

		if ((groupId + BenchmarksPropsValues.MAX_USER_TO_GROUP_COUNT) >
				BenchmarksPropsValues.MAX_GROUP_COUNT) {

			groupId =
				groupId - BenchmarksPropsValues.MAX_USER_TO_GROUP_COUNT + 1;
		}

		for (int i = 0; i < BenchmarksPropsValues.MAX_USER_TO_GROUP_COUNT;
			 i++) {

			groupIds.add(groupId + i);
		}

		return groupIds;
	}

	public long getNextAssetClassNameId(long groupId) {
		Integer index = _assetClassNameIdsIndexes.get(groupId);

		if (index == null) {
			index = 0;
		}

		long classNameId =
			_assetClassNameIds[index % _assetClassNameIds.length];

		_assetClassNameIdsIndexes.put(groupId, ++index);

		return classNameId;
	}

	public String getPortletId(String portletPrefix) {
		return portletPrefix.concat(PortletIdCodec.generateInstanceId());
	}

	public RoleModel getPowerUserRoleModel() {
		return _powerUserRoleModel;
	}

	public int getRandomCProductModelIndex() {
		Random random = new Random();

		int count = (int)Math.ceil(
			BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_COUNT /
				BenchmarksPropsValues.MAX_COMMERCE_CATALOG_COUNT);

		if (BenchmarksPropsValues.MAX_COMMERCE_CATALOG_COUNT >
				BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_COUNT) {

			count = BenchmarksPropsValues.MAX_COMMERCE_CATALOG_COUNT;
		}

		return random.nextInt(count);
	}

	public List<Integer> getSequence(int size) {
		List<Integer> sequence = new ArrayList<>(size);

		for (int i = 1; i <= size; i++) {
			sequence.add(i);
		}

		return sequence;
	}

	public RoleModel getUserRoleModel() {
		return _userRoleModel;
	}

	public void initJournalArticleContent() {
		int maxJournalArticleSize =
			BenchmarksPropsValues.MAX_JOURNAL_ARTICLE_SIZE;

		if (maxJournalArticleSize <= 0) {
			maxJournalArticleSize = 1;
		}

		char[] chars = new char[maxJournalArticleSize];

		for (int i = 0; i < maxJournalArticleSize; i++) {
			chars[i] = (char)(CharPool.LOWER_CASE_A + (i % 26));
		}

		_journalArticleContent = new String(chars);
	}

	public List<CommerceOrderModel> newAccountEntryCommerceOrderModels(
		long groupId, long accountEntryId, long commerceBillingAddressId,
		String commerceCurrencyCode, int commerceOrderStatus,
		long commerceShippingAddressId, long commerceShippingMethodId,
		String commerceShippingOptionName) {

		List<CommerceOrderModel> commerceOrderModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_ACCOUNT_ENTRY_COMMERCE_ORDER_COUNT);

		for (int i = 1;
			 i <= BenchmarksPropsValues.MAX_ACCOUNT_ENTRY_COMMERCE_ORDER_COUNT;
			 i++) {

			commerceOrderModels.add(
				newCommerceOrderModel(
					groupId, accountEntryId, commerceBillingAddressId,
					commerceCurrencyCode, commerceOrderStatus,
					commerceShippingAddressId, commerceShippingMethodId,
					commerceShippingOptionName));
		}

		return commerceOrderModels;
	}

	public GroupModel newAccountEntryGroupModel(
		AccountEntryModel accountEntryModel) {

		return newGroupModel(
			getClassNameId(AccountEntry.class),
			accountEntryModel.getAccountEntryId(), _counter.get(),
			accountEntryModel.getName(), GroupConstants.TYPE_SITE_PRIVATE,
			StringPool.BLANK, false);
	}

	public AccountEntryModel newAccountEntryModel(String type, int index) {
		AccountEntryModel accountEntryModel = new AccountEntryModelImpl();

		// PK fields

		accountEntryModel.setAccountEntryId(_counter.get());

		// Audit fields

		accountEntryModel.setCompanyId(_companyId);
		accountEntryModel.setUserId(_sampleUserId);
		accountEntryModel.setUserName(_SAMPLE_USER_NAME);
		accountEntryModel.setCreateDate(new Date());
		accountEntryModel.setModifiedDate(new Date());

		// Other fields

		accountEntryModel.setDefaultBillingAddressId(0);
		accountEntryModel.setDefaultShippingAddressId(0);
		accountEntryModel.setParentAccountEntryId(0);
		accountEntryModel.setDescription(null);
		accountEntryModel.setDomains(null);
		accountEntryModel.setEmailAddress(null);
		accountEntryModel.setName("Account Entry" + index);
		accountEntryModel.setTaxExemptionCode(null);
		accountEntryModel.setTaxIdNumber(null);
		accountEntryModel.setLogoId(0);
		accountEntryModel.setType("business");
		accountEntryModel.setStatus(0);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		accountEntryModel.setUuid(uuid);
		accountEntryModel.setExternalReferenceCode(uuid);

		return accountEntryModel;
	}

	public List<AccountEntryModel> newAccountEntryModels() {
		List<AccountEntryModel> accountEntryModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_ACCOUNT_ENTRY_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_ACCOUNT_ENTRY_COUNT;
			 i++) {

			accountEntryModels.add(newAccountEntryModel("business", i));
		}

		return accountEntryModels;
	}

	public AccountEntryUserRelModel newAccountEntryUserRelModel(
		UserModel user, long accountEntryId) {

		AccountEntryUserRelModel accountEntryUserRelModel =
			new AccountEntryUserRelModelImpl();

		// PK fields

		accountEntryUserRelModel.setAccountEntryUserRelId(_counter.get());

		// Audit fields

		accountEntryUserRelModel.setCompanyId(_companyId);

		// Other fields

		accountEntryUserRelModel.setAccountEntryId(accountEntryId);
		accountEntryUserRelModel.setAccountUserId(user.getUserId());

		return accountEntryUserRelModel;
	}

	public AddressModel newAddressModel(long accountEntryId, long countryId) {
		AddressModel addressModel = new AddressModelImpl();

		// PK fields

		addressModel.setAddressId(_counter.get());

		// Audit fields

		addressModel.setCompanyId(_companyId);
		addressModel.setUserId(_sampleUserId);
		addressModel.setUserName(_SAMPLE_USER_NAME);
		addressModel.setCreateDate(new Date());
		addressModel.setModifiedDate(new Date());

		// Other fields

		addressModel.setClassNameId(getClassNameId(AccountEntry.class));
		addressModel.setClassPK(accountEntryId);
		addressModel.setCountryId(countryId);
		addressModel.setListTypeId(14001);
		addressModel.setCity("Los Angeles");
		addressModel.setDescription(null);
		addressModel.setName("Sample Address");
		addressModel.setStreet1("123 Sample Street");
		addressModel.setZip("1234");

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		addressModel.setUuid(uuid);
		addressModel.setExternalReferenceCode(uuid);

		if (_firstAddressModel == null) {
			_firstAddressModel = addressModel;
		}

		return addressModel;
	}

	public List<AssetCategoryModel> newAssetCategoryModels(
		long groupId, List<AssetVocabularyModel> assetVocabularyModels) {

		List<AssetCategoryModel> assetCategoryModels = new ArrayList<>();

		List<AssetCategoryModel> groupAssetCategoryModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_ASSET_VUCABULARY_COUNT *
				BenchmarksPropsValues.MAX_ASSET_CATEGORY_COUNT);

		for (AssetVocabularyModel assetVocabularyModel :
				assetVocabularyModels) {

			for (int k = 0; k < BenchmarksPropsValues.MAX_ASSET_CATEGORY_COUNT;
				 k++) {

				AssetCategoryModel assetCategoryModel = newAssetCategoryModel(
					groupId,
					StringBundler.concat(
						"TestCategory_", assetVocabularyModel.getVocabularyId(),
						StringPool.UNDERLINE, k),
					assetVocabularyModel.getVocabularyId());

				groupAssetCategoryModels.add(assetCategoryModel);

				assetCategoryModels.add(assetCategoryModel);
			}
		}

		Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap =
			new HashMap<>();

		int pageSize =
			groupAssetCategoryModels.size() / _assetClassNameIds.length;

		for (int j = 0; j < _assetClassNameIds.length; j++) {
			int fromIndex = j * pageSize;

			int toIndex = (j + 1) * pageSize;

			if (j == (_assetClassNameIds.length - 1)) {
				toIndex = groupAssetCategoryModels.size();
			}

			assetCategoryModelsMap.put(
				_assetClassNameIds[j],
				groupAssetCategoryModels.subList(fromIndex, toIndex));
		}

		_assetCategoryModelsMaps[(int)groupId - 1] = assetCategoryModelsMap;

		return assetCategoryModels;
	}

	public AssetEntryModel newAssetEntryModel(BlogsEntryModel blogsEntryModel) {
		return newAssetEntryModel(
			blogsEntryModel.getGroupId(), blogsEntryModel.getCreateDate(),
			blogsEntryModel.getModifiedDate(), getClassNameId(BlogsEntry.class),
			blogsEntryModel.getEntryId(), blogsEntryModel.getUuid(), 0, true,
			true, ContentTypes.TEXT_HTML, blogsEntryModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(
		DLFileEntryModel dlFileEntryModel) {

		return newAssetEntryModel(
			dlFileEntryModel.getGroupId(), dlFileEntryModel.getCreateDate(),
			dlFileEntryModel.getModifiedDate(),
			getClassNameId(DLFileEntry.class),
			dlFileEntryModel.getFileEntryId(), dlFileEntryModel.getUuid(),
			dlFileEntryModel.getFileEntryTypeId(), true, true,
			dlFileEntryModel.getMimeType(), dlFileEntryModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(DLFolderModel dlFolderModel) {
		return newAssetEntryModel(
			dlFolderModel.getGroupId(), dlFolderModel.getCreateDate(),
			dlFolderModel.getModifiedDate(), getClassNameId(DLFolder.class),
			dlFolderModel.getFolderId(), dlFolderModel.getUuid(), 0, true, true,
			null, dlFolderModel.getName());
	}

	public AssetEntryModel newAssetEntryModel(MBMessageModel mbMessageModel) {
		long classNameId = 0;
		boolean visible = false;

		if (mbMessageModel.getCategoryId() ==
				MBCategoryConstants.DISCUSSION_CATEGORY_ID) {

			classNameId = getClassNameId(MBDiscussion.class);
		}
		else {
			classNameId = getClassNameId(MBMessage.class);
			visible = true;
		}

		return newAssetEntryModel(
			mbMessageModel.getGroupId(), mbMessageModel.getCreateDate(),
			mbMessageModel.getModifiedDate(), classNameId,
			mbMessageModel.getMessageId(), mbMessageModel.getUuid(), 0, true,
			visible, ContentTypes.TEXT_HTML, mbMessageModel.getSubject());
	}

	public AssetEntryModel newAssetEntryModel(MBThreadModel mbThreadModel) {
		return newAssetEntryModel(
			mbThreadModel.getGroupId(), mbThreadModel.getCreateDate(),
			mbThreadModel.getModifiedDate(), getClassNameId(MBThread.class),
			mbThreadModel.getThreadId(), mbThreadModel.getUuid(), 0, true,
			false, StringPool.BLANK,
			String.valueOf(mbThreadModel.getRootMessageId()));
	}

	public AssetEntryModel newAssetEntryModel(
		ObjectEntryModel objectEntryModel) {

		return newAssetEntryModel(
			objectEntryModel.getGroupId(), objectEntryModel.getCreateDate(),
			objectEntryModel.getModifiedDate(),
			getClassNameId(
				ObjectDefinitionConstants.
					CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
						objectEntryModel.getObjectDefinitionId()),
			objectEntryModel.getObjectEntryId(), objectEntryModel.getUuid(), 0,
			true, objectEntryModel.isApproved(), ContentTypes.TEXT_PLAIN,
			String.valueOf(objectEntryModel.getObjectEntryId()));
	}

	public AssetEntryModel newAssetEntryModel(
		ObjectValuePair<JournalArticleModel, JournalArticleLocalizationModel>
			objectValuePair) {

		JournalArticleModel journalArticleModel = objectValuePair.getKey();
		JournalArticleLocalizationModel journalArticleLocalizationModel =
			objectValuePair.getValue();

		long resourcePrimKey = journalArticleModel.getResourcePrimKey();

		String resourceUUID = _journalArticleResourceUUIDs.get(resourcePrimKey);

		_journalArticleAssetEntryModel = newAssetEntryModel(
			journalArticleModel.getGroupId(),
			journalArticleModel.getCreateDate(),
			journalArticleModel.getModifiedDate(),
			getClassNameId(JournalArticle.class), resourcePrimKey, resourceUUID,
			_defaultJournalDDMStructureId, journalArticleModel.isIndexable(),
			true, ContentTypes.TEXT_HTML,
			journalArticleLocalizationModel.getTitle());

		return _journalArticleAssetEntryModel;
	}

	public AssetListEntryModel newAssetListEntryModel(long groupId, int index) {
		AssetListEntryModel assetListEntryModel = new AssetListEntryModelImpl();

		// PK fields

		assetListEntryModel.setAssetListEntryId(_counter.get());

		// Group instance

		assetListEntryModel.setGroupId(groupId);

		// Audit fields

		assetListEntryModel.setCompanyId(_companyId);
		assetListEntryModel.setUserId(_sampleUserId);
		assetListEntryModel.setUserName(_SAMPLE_USER_NAME);
		assetListEntryModel.setCreateDate(new Date());
		assetListEntryModel.setModifiedDate(new Date());

		// Other fields

		String title = StringBundler.concat(
			"dynamic-collection-", groupId, "-", index);

		assetListEntryModel.setAssetListEntryKey(title);
		assetListEntryModel.setTitle(title);

		assetListEntryModel.setType(AssetListEntryTypeConstants.TYPE_DYNAMIC);
		assetListEntryModel.setAssetEntrySubtype(null);
		assetListEntryModel.setAssetEntryType(AssetEntry.class.getName());
		assetListEntryModel.setLastPublishDate(null);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		assetListEntryModel.setUuid(uuid);
		assetListEntryModel.setExternalReferenceCode(uuid);

		return assetListEntryModel;
	}

	public AssetListEntrySegmentsEntryRelModel
		newAssetListEntrySegmentsEntryRelModel(
			AssetListEntryModel assetListEntryModel,
			DDMStructureModel ddmStructureModel, int index) {

		AssetListEntrySegmentsEntryRelModel
			assetListEntrySegmentsEntryRelModel =
				new AssetListEntrySegmentsEntryRelModelImpl();

		// PK fields

		assetListEntrySegmentsEntryRelModel.setAssetListEntrySegmentsEntryRelId(
			_counter.get());

		// Group instance

		long groupId = assetListEntryModel.getGroupId();

		assetListEntrySegmentsEntryRelModel.setGroupId(groupId);

		// Audit fields

		assetListEntrySegmentsEntryRelModel.setCompanyId(_companyId);
		assetListEntrySegmentsEntryRelModel.setUserId(_sampleUserId);
		assetListEntrySegmentsEntryRelModel.setUserName(_SAMPLE_USER_NAME);
		assetListEntrySegmentsEntryRelModel.setCreateDate(new Date());
		assetListEntrySegmentsEntryRelModel.setModifiedDate(new Date());

		// Other fields

		assetListEntrySegmentsEntryRelModel.setAssetListEntryId(
			assetListEntryModel.getAssetListEntryId());
		assetListEntrySegmentsEntryRelModel.setPriority(0);
		assetListEntrySegmentsEntryRelModel.setSegmentsEntryId(
			SegmentsEntryConstants.ID_DEFAULT);

		Map<String, String> map = HashMapBuilder.<String, String>create(
			11
		).put(
			"anyAssetType", Boolean.FALSE.toString()
		).put(
			"classNameIds",
			_assetClassNameIds[0] + StringPool.COMMA + _assetClassNameIds[1]
		).put(
			"classTypeIdsDLFileEntryAssetRendererFactory",
			String.valueOf(_DEFAULT_DL_FILE_ENTRY_TYPE_ID)
		).put(
			"classTypeIdsJournalArticleAssetRendererFactory",
			String.valueOf(ddmStructureModel.getStructureId())
		).put(
			"groupIds", String.valueOf(groupId)
		).put(
			"orderByColumn1", "modifiedDate"
		).put(
			"orderByColumn2", "title"
		).put(
			"orderByType1", "DESC"
		).put(
			"orderByType2", "ASC"
		).put(
			"subtypeFieldsFilterEnabledDLFileEntryAssetRendererFactory",
			Boolean.FALSE.toString()
		).put(
			"subtypeFieldsFilterEnabledJournalArticleAssetRendererFactory",
			Boolean.FALSE.toString()
		).build();

		if (index == 1) {
			map.put("queryAndOperator0", Boolean.TRUE.toString());
			map.put("queryContains0", Boolean.TRUE.toString());
			map.put("queryName0", "assetTags");
		}
		else {
			String assetPublisherQueryName = "assetCategories";

			if ((index % 2) == 0) {
				assetPublisherQueryName = "assetTags";
			}

			ObjectValuePair<String[], Integer> objectValuePair = null;

			Integer startIndex = _assetPublisherQueryStartIndexes.get(groupId);

			if (startIndex == null) {
				startIndex = 0;
			}

			if (assetPublisherQueryName.equals("assetCategories")) {
				Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap =
					_assetCategoryModelsMaps[(int)groupId - 1];

				List<AssetCategoryModel> assetCategoryModels =
					assetCategoryModelsMap.get(
						getNextAssetClassNameId(groupId));

				objectValuePair = getAssetPublisherAssetCategoriesQueryValues(
					assetCategoryModels, startIndex);
			}
			else {
				Map<Long, List<AssetTagModel>> assetTagModelsMap =
					_assetTagModelsMaps[(int)groupId - 1];

				List<AssetTagModel> assetTagModels = assetTagModelsMap.get(
					getNextAssetClassNameId(groupId));

				objectValuePair = getAssetPublisherAssetTagsQueryValues(
					assetTagModels, startIndex);
			}

			String[] assetPublisherQueryValues = objectValuePair.getKey();

			map.put("queryAndOperator0", Boolean.FALSE.toString());
			map.put("queryAndOperator1", Boolean.FALSE.toString());
			map.put("queryContains0", Boolean.TRUE.toString());
			map.put("queryContains1", Boolean.FALSE.toString());
			map.put("queryName0", assetPublisherQueryName);
			map.put("queryName1", assetPublisherQueryName);
			map.put(
				"queryValues0",
				StringBundler.concat(
					assetPublisherQueryValues[0], StringPool.COMMA,
					assetPublisherQueryValues[1], StringPool.COMMA,
					assetPublisherQueryValues[2]));
			map.put("queryValues1", assetPublisherQueryValues[3]);
		}

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.create(
			map, true
		).build();

		assetListEntrySegmentsEntryRelModel.setTypeSettings(
			unicodeProperties.toString());

		assetListEntrySegmentsEntryRelModel.setLastPublishDate(null);

		// Autogenerated fields

		assetListEntrySegmentsEntryRelModel.setUuid(SequentialUUID.generate());

		return assetListEntrySegmentsEntryRelModel;
	}

	public AssetListEntryUsageModel newAssetListEntryUsageModel(
		AssetListEntryModel assetListEntryModel, String portletId,
		LayoutModel layoutModel) {

		AssetListEntryUsageModel assetListEntryUsageModel =
			new AssetListEntryUsageModelImpl();

		// PK fields

		assetListEntryUsageModel.setAssetListEntryUsageId(_counter.get());

		// Group instance

		assetListEntryUsageModel.setGroupId(assetListEntryModel.getGroupId());

		// Audit fields

		assetListEntryUsageModel.setCompanyId(_companyId);
		assetListEntryUsageModel.setUserId(_sampleUserId);
		assetListEntryUsageModel.setUserName(_SAMPLE_USER_NAME);
		assetListEntryUsageModel.setCreateDate(new Date());
		assetListEntryUsageModel.setModifiedDate(new Date());

		// Other fields

		assetListEntryUsageModel.setClassNameId(
			getClassNameId(AssetListEntry.class));
		assetListEntryUsageModel.setContainerKey(portletId);
		assetListEntryUsageModel.setContainerType(
			getClassNameId(Portlet.class));
		assetListEntryUsageModel.setKey(
			String.valueOf(assetListEntryModel.getAssetListEntryId()));
		assetListEntryUsageModel.setPlid(layoutModel.getPlid());
		assetListEntryUsageModel.setType(
			AssetListEntryUsageConstants.TYPE_LAYOUT);
		assetListEntryUsageModel.setLastPublishDate(null);

		// Autogenerated fields

		assetListEntryUsageModel.setUuid(SequentialUUID.generate());

		return assetListEntryUsageModel;
	}

	public List<PortletPreferencesModel>
		newAssetPublisherPortletPreferencesModels(long plid) {

		return ListUtil.fromArray(
			newPortletPreferencesModel(plid, BlogsPortletKeys.BLOGS),
			newPortletPreferencesModel(plid, JournalPortletKeys.JOURNAL));
	}

	public PortletPreferenceValueModel
		newAssetPublisherPortletPreferenceValueModels(
			AssetListEntryModel assetListEntryModel,
			PortletPreferencesModel portletPreferencesModel) {

		return newPortletPreferenceValueModel(
			portletPreferencesModel, "assetListEntryExternalReferenceCode", 0,
			assetListEntryModel.getUuid());
	}

	public List<AssetTagModel> newAssetTagModels(long groupId) {
		List<AssetTagModel> assetTagModels = new ArrayList<>();

		List<AssetTagModel> groupAssetTagModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_ASSET_TAG_COUNT);

		for (int j = 0; j < BenchmarksPropsValues.MAX_ASSET_TAG_COUNT; j++) {
			AssetTagModel assetTagModel = new AssetTagModelImpl();

			// PK fields

			assetTagModel.setTagId(_counter.get());

			// Group instance

			assetTagModel.setGroupId(groupId);

			// Audit fields

			assetTagModel.setCompanyId(_companyId);
			assetTagModel.setUserId(_sampleUserId);
			assetTagModel.setUserName(_SAMPLE_USER_NAME);
			assetTagModel.setCreateDate(new Date());
			assetTagModel.setModifiedDate(new Date());

			// Other fields

			assetTagModel.setName(
				StringBundler.concat("TestTag_", groupId, "_", j));
			assetTagModel.setLastPublishDate(new Date());

			// Autogenerated fields

			String uuid = SequentialUUID.generate();

			assetTagModel.setUuid(uuid);
			assetTagModel.setExternalReferenceCode(uuid);

			groupAssetTagModels.add(assetTagModel);

			assetTagModels.add(assetTagModel);
		}

		Map<Long, List<AssetTagModel>> assetTagModelsMap = new HashMap<>();

		int pageSize = groupAssetTagModels.size() / _assetClassNameIds.length;

		for (int j = 0; j < _assetClassNameIds.length; j++) {
			int fromIndex = j * pageSize;

			int toIndex = (j + 1) * pageSize;

			if (j == (_assetClassNameIds.length - 1)) {
				toIndex = groupAssetTagModels.size();
			}

			assetTagModelsMap.put(
				_assetClassNameIds[j],
				groupAssetTagModels.subList(fromIndex, toIndex));
		}

		_assetTagModelsMaps[(int)groupId - 1] = assetTagModelsMap;

		return assetTagModels;
	}

	public List<AssetVocabularyModel> newAssetVocabularyModels(long groupId) {
		List<AssetVocabularyModel> assetVocabularyModels = new ArrayList<>();

		for (int j = 0; j < BenchmarksPropsValues.MAX_ASSET_VUCABULARY_COUNT;
			 j++) {

			AssetVocabularyModel assetVocabularyModel = newAssetVocabularyModel(
				groupId, _sampleUserId, _SAMPLE_USER_NAME,
				StringBundler.concat(
					"TestVocabulary_", groupId, StringPool.UNDERLINE, j));

			assetVocabularyModels.add(assetVocabularyModel);
		}

		return assetVocabularyModels;
	}

	public List<BlogsEntryModel> newBlogsEntryModels(long groupId) {
		List<BlogsEntryModel> blogEntryModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_BLOGS_ENTRY_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_BLOGS_ENTRY_COUNT; i++) {
			blogEntryModels.add(newBlogsEntryModel(groupId, i));
		}

		return blogEntryModels;
	}

	public PortletPreferencesModel
		newCommerceB2BSiteTypePortletPreferencesModel(long ownerId) {

		return newPortletPreferencesModel(
			ownerId, PortletKeys.PREFS_OWNER_TYPE_GROUP, 0,
			CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT);
	}

	public PortletPreferenceValueModel
		newCommerceB2BSiteTypePortletPreferenceValueModel(
			PortletPreferencesModel portletPreferencesModel) {

		return newPortletPreferenceValueModel(
			portletPreferencesModel, "commerceSiteType", 0,
			String.valueOf(CommerceChannelConstants.SITE_TYPE_B2B));
	}

	public GroupModel newCommerceCatalogGroupModel(
		CommerceCatalogModel commerceCatalogModel) {

		return newGroupModel(
			getClassNameId(CommerceCatalog.class),
			commerceCatalogModel.getCommerceCatalogId(), _counter.get(),
			commerceCatalogModel.getName(), false);
	}

	public CommerceCatalogModel newCommerceCatalogModel(
		CommerceCurrencyModel commerceCurrencyModel, int count) {

		CommerceCatalogModel commerceCatalogModel =
			new CommerceCatalogModelImpl();

		// PK fields

		commerceCatalogModel.setCommerceCatalogId(_counter.get());

		// Audit fields

		commerceCatalogModel.setCompanyId(_companyId);
		commerceCatalogModel.setUserId(_sampleUserId);
		commerceCatalogModel.setUserName(_SAMPLE_USER_NAME);
		commerceCatalogModel.setCreateDate(new Date());
		commerceCatalogModel.setModifiedDate(new Date());

		// Other fields

		commerceCatalogModel.setName("Master" + count);
		commerceCatalogModel.setCommerceCurrencyCode(
			commerceCurrencyModel.getCode());
		commerceCatalogModel.setCatalogDefaultLanguageId("en_US");
		commerceCatalogModel.setSystem(true);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		commerceCatalogModel.setUuid(uuid);
		commerceCatalogModel.setExternalReferenceCode(uuid);

		return commerceCatalogModel;
	}

	public List<CommerceCatalogModel> newCommerceCatalogModels(
		CommerceCurrencyModel commerceCurrencyModel) {

		List<CommerceCatalogModel> commerceCatalogModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_COMMERCE_CATALOG_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_COMMERCE_CATALOG_COUNT;
			 i++) {

			commerceCatalogModels.add(
				newCommerceCatalogModel(commerceCurrencyModel, i));
		}

		return commerceCatalogModels;
	}

	public ResourcePermissionModel newCommerceCatalogResourcePermissionModel(
		CommerceCatalogModel commerceCatalogModel) {

		return newResourcePermissionModel(
			CommerceCatalog.class.getName(),
			String.valueOf(commerceCatalogModel.getCommerceCatalogId()),
			_guestRoleModel.getRoleId(), _sampleUserId);
	}

	public GroupModel newCommerceChannelGroupModel(
		CommerceChannelModel commerceChannelModel) {

		return newGroupModel(
			getClassNameId(CommerceChannel.class),
			commerceChannelModel.getCommerceChannelId(), _counter.get(),
			commerceChannelModel.getName(), false);
	}

	public List<GroupModel> newCommerceChannelGroupModels(
		List<CommerceChannelModel> commerceChannelModels) {

		List<GroupModel> groupModels = new ArrayList<>(
			commerceChannelModels.size());

		for (CommerceChannelModel commerceChannelModel :
				commerceChannelModels) {

			groupModels.add(
				newGroupModel(
					getClassNameId(CommerceChannel.class),
					commerceChannelModel.getCommerceChannelId(), _counter.get(),
					commerceChannelModel.getName(), false));
		}

		return groupModels;
	}

	public CommerceChannelModel newCommerceChannelModel(
		long groupId, CommerceCurrencyModel commerceCurrencyModel, int count) {

		CommerceChannelModel commerceChannelModel =
			new CommerceChannelModelImpl();

		// PK fields

		commerceChannelModel.setCommerceChannelId(_counter.get());

		// Audit fields

		commerceChannelModel.setCompanyId(_companyId);
		commerceChannelModel.setUserId(_sampleUserId);
		commerceChannelModel.setUserName(_SAMPLE_USER_NAME);
		commerceChannelModel.setCreateDate(new Date());
		commerceChannelModel.setModifiedDate(new Date());

		// Other fields

		commerceChannelModel.setSiteGroupId(groupId);
		commerceChannelModel.setName(_SAMPLE_USER_NAME + " Channel" + count);
		commerceChannelModel.setType("site");
		commerceChannelModel.setTypeSettings(String.valueOf(_guestGroupId));
		commerceChannelModel.setCommerceCurrencyCode(
			commerceCurrencyModel.getCode());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		commerceChannelModel.setUuid(uuid);
		commerceChannelModel.setExternalReferenceCode(uuid);

		return commerceChannelModel;
	}

	public List<CommerceChannelModel> newCommerceChannelModels(
		List<GroupModel> groupModels,
		CommerceCurrencyModel commerceCurrencyModel) {

		List<CommerceChannelModel> commerceChannelModels = new ArrayList<>(
			groupModels.size());

		for (int i = 1; i <= groupModels.size(); i++) {
			GroupModel groupModel = groupModels.get(i - 1);

			commerceChannelModels.add(
				newCommerceChannelModel(
					groupModel.getGroupId(), commerceCurrencyModel, i));
		}

		return commerceChannelModels;
	}

	public CommerceChannelRelModel newCommerceChannelRelModel(
		long classNameId, long classPK, long commerceChannelId) {

		CommerceChannelRelModel commerceChannelRelModel =
			new CommerceChannelRelModelImpl();

		// PK fields

		commerceChannelRelModel.setCommerceChannelRelId(_counter.get());

		// Audit fields

		commerceChannelRelModel.setCompanyId(_companyId);
		commerceChannelRelModel.setUserId(_sampleUserId);
		commerceChannelRelModel.setUserName(_SAMPLE_USER_NAME);
		commerceChannelRelModel.setCreateDate(new Date());
		commerceChannelRelModel.setModifiedDate(new Date());

		// Other fields

		commerceChannelRelModel.setClassNameId(classNameId);
		commerceChannelRelModel.setClassPK(classPK);
		commerceChannelRelModel.setCommerceChannelId(commerceChannelId);

		return commerceChannelRelModel;
	}

	public CommerceCurrencyModel newCommerceCurrencyModel() {
		CommerceCurrencyModel commerceCurrencyModel =
			new CommerceCurrencyModelImpl();

		commerceCurrencyModel.setUuid(SequentialUUID.generate());

		// PK fields

		commerceCurrencyModel.setCommerceCurrencyId(_counter.get());

		// Audit fields

		commerceCurrencyModel.setCompanyId(_companyId);
		commerceCurrencyModel.setUserId(_sampleUserId);
		commerceCurrencyModel.setUserName(_SAMPLE_USER_NAME);
		commerceCurrencyModel.setCreateDate(new Date());
		commerceCurrencyModel.setModifiedDate(new Date());

		// Other fields

		commerceCurrencyModel.setCode("USD");
		commerceCurrencyModel.setName(
			StringBundler.concat(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root available-",
				"locales=\"en_US\" default-locale=\"en_US\"><Name language-id=",
				"\"en_US\">US Dollar</Name></root>"));
		commerceCurrencyModel.setRate(BigDecimal.valueOf(1));
		commerceCurrencyModel.setFormatPattern(
			StringBundler.concat(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root ",
				"available-locales=\"en_US\" default-locale=\"en_US\">",
				"<FormatPattern language-id=\"en_US\">$###,##0.00",
				"</FormatPattern></root>"));
		commerceCurrencyModel.setMaxFractionDigits(2);
		commerceCurrencyModel.setMinFractionDigits(2);
		commerceCurrencyModel.setRoundingMode("HALF_EVEN");
		commerceCurrencyModel.setPrimary(true);
		commerceCurrencyModel.setPriority(1);
		commerceCurrencyModel.setActive(true);
		commerceCurrencyModel.setLastPublishDate(new Date());

		// Autogenerated fields

		commerceCurrencyModel.setUuid(SequentialUUID.generate());

		return commerceCurrencyModel;
	}

	public List<GroupModel> newCommerceGroupModels() {
		List<GroupModel> groupModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_COMMERCE_GROUP_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_COMMERCE_GROUP_COUNT;
			 i++) {

			long id = _counter.get();

			groupModels.add(
				newGroupModel(
					getClassNameId(Group.class), id, id, "Commerce Site " + i,
					true));
		}

		return groupModels;
	}

	public CommerceInventoryWarehouseItemModel
		newCommerceInventoryWarehouseItemModel(
			CommerceInventoryWarehouseModel commerceInventoryWarehouseModel,
			CPInstanceModel cpInstanceModel) {

		CommerceInventoryWarehouseItemModel
			commerceInventoryWarehouseItemModel =
				new CommerceInventoryWarehouseItemModelImpl();

		// PK fields

		commerceInventoryWarehouseItemModel.setCommerceInventoryWarehouseItemId(
			_counter.get());

		// Audit fields

		commerceInventoryWarehouseItemModel.setCompanyId(_companyId);
		commerceInventoryWarehouseItemModel.setUserId(_sampleUserId);
		commerceInventoryWarehouseItemModel.setUserName(_SAMPLE_USER_NAME);
		commerceInventoryWarehouseItemModel.setCreateDate(new Date());
		commerceInventoryWarehouseItemModel.setModifiedDate(new Date());

		// Other fields

		commerceInventoryWarehouseItemModel.setCommerceInventoryWarehouseId(
			commerceInventoryWarehouseModel.getCommerceInventoryWarehouseId());
		commerceInventoryWarehouseItemModel.setSku(cpInstanceModel.getSku());
		commerceInventoryWarehouseItemModel.setQuantity(
			BigDecimal.valueOf(
				BenchmarksPropsValues.
					MAX_COMMERCE_INVENTORY_WAREHOUSE_ITEM_QUANTITY));
		commerceInventoryWarehouseItemModel.setReservedQuantity(
			BigDecimal.ZERO);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		commerceInventoryWarehouseItemModel.setUuid(uuid);
		commerceInventoryWarehouseItemModel.setExternalReferenceCode(uuid);

		return commerceInventoryWarehouseItemModel;
	}

	public CommerceInventoryWarehouseModel newCommerceInventoryWarehouseModel(
		int index) {

		CommerceInventoryWarehouseModel commerceInventoryWarehouseModel =
			new CommerceInventoryWarehouseModelImpl();

		// PK fields

		long warehouseId = _counter.get();

		commerceInventoryWarehouseModel.setCommerceInventoryWarehouseId(
			warehouseId);

		// Audit fields

		commerceInventoryWarehouseModel.setCompanyId(_companyId);
		commerceInventoryWarehouseModel.setUserId(_sampleUserId);
		commerceInventoryWarehouseModel.setUserName(_SAMPLE_USER_NAME);
		commerceInventoryWarehouseModel.setCreateDate(new Date());
		commerceInventoryWarehouseModel.setModifiedDate(new Date());

		// Other fields

		commerceInventoryWarehouseModel.setName("Warehouse " + index);
		commerceInventoryWarehouseModel.setDescription(
			"Description for warehouse with ID " + warehouseId);
		commerceInventoryWarehouseModel.setActive(true);
		commerceInventoryWarehouseModel.setStreet1("Street1");
		commerceInventoryWarehouseModel.setStreet2("Street2");
		commerceInventoryWarehouseModel.setStreet3("Street3");
		commerceInventoryWarehouseModel.setCity("City");
		commerceInventoryWarehouseModel.setZip("Zip");
		commerceInventoryWarehouseModel.setCommerceRegionCode("CA");
		commerceInventoryWarehouseModel.setCountryTwoLettersISOCode("US");
		commerceInventoryWarehouseModel.setLatitude(0);
		commerceInventoryWarehouseModel.setLongitude(0);
		commerceInventoryWarehouseModel.setType(null);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		commerceInventoryWarehouseModel.setUuid(uuid);
		commerceInventoryWarehouseModel.setExternalReferenceCode(uuid);

		return commerceInventoryWarehouseModel;
	}

	public List<CommerceInventoryWarehouseModel>
		newCommerceInventoryWarehouseModels() {

		List<CommerceInventoryWarehouseModel> commerceInventoryWarehouseModels =
			new ArrayList<>(
				BenchmarksPropsValues.MAX_COMMERCE_INVENTORY_WAREHOUSE_COUNT);

		for (int i = 1;
			 i <= BenchmarksPropsValues.MAX_COMMERCE_INVENTORY_WAREHOUSE_COUNT;
			 i++) {

			commerceInventoryWarehouseModels.add(
				newCommerceInventoryWarehouseModel(i));
		}

		return commerceInventoryWarehouseModels;
	}

	public List<LayoutModel> newCommerceLayoutModels(long groupId)
		throws Exception {

		List<LayoutModel> layoutModels = new ArrayList<>();

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			StringUtil.read(
				getResourceInputStream("commerce/commerce_layouts.json")));

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			LayoutModel layoutModel = newLayoutModel(
				groupId, jsonObject.getString("layoutTemplateId"),
				StringUtil.replace(
					StringUtil.toLowerCase(jsonObject.getString("name")),
					CharPool.SPACE, CharPool.DASH),
				jsonObject.getBoolean("privateLayout"),
				getPortletNames(jsonObject.getJSONArray("portlets")));

			layoutModels.add(layoutModel);

			JSONArray subLayoutsJSONArray = jsonObject.getJSONArray(
				"subLayouts");

			if ((subLayoutsJSONArray != null) &&
				(subLayoutsJSONArray.length() > 0)) {

				for (int k = 0; k < subLayoutsJSONArray.length(); k++) {
					JSONObject sublayoutJSONObject =
						subLayoutsJSONArray.getJSONObject(k);

					layoutModels.add(
						newLayoutModel(
							groupId, sublayoutJSONObject.getBoolean("hidden"),
							sublayoutJSONObject.getString("layoutTemplateId"),
							StringUtil.replace(
								StringUtil.toLowerCase(
									sublayoutJSONObject.getString("name")),
								CharPool.SPACE, CharPool.DASH),
							sublayoutJSONObject.getBoolean("privateLayout"),
							layoutModel.getLayoutId(),
							getPortletNames(
								sublayoutJSONObject.getJSONArray("portlets"))));
				}
			}
		}

		return layoutModels;
	}

	public List<PortletPreferenceValueModel>
			newCommerceLayoutPortletPreferenceValueModels(
				List<PortletPreferencesModel> portletPreferencesModels)
		throws Exception {

		List<PortletPreferenceValueModel> portletPreferenceValueModels =
			new ArrayList<>();

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			StringUtil.read(
				getResourceInputStream("commerce/commerce_layouts.json")));

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			portletPreferenceValueModels.addAll(
				newCommercePortletPreferenceValueModels(
					portletPreferencesModels,
					jsonObject.getJSONArray("portlets")));

			JSONArray sublayoutsJSONArray = jsonObject.getJSONArray(
				"subLayouts");

			if (sublayoutsJSONArray != null) {
				for (int j = 0; j < sublayoutsJSONArray.length(); j++) {
					JSONObject sublayoutJSONObject =
						sublayoutsJSONArray.getJSONObject(j);

					portletPreferenceValueModels.addAll(
						newCommercePortletPreferenceValueModels(
							portletPreferencesModels,
							sublayoutJSONObject.getJSONArray("portlets")));
				}
			}
		}

		portletPreferenceValueModels.addAll(
			newCommercePortletPreferenceValueModels(
				portletPreferencesModels,
				JSONFactoryUtil.createJSONArray(
					StringUtil.read(
						getResourceInputStream(
							"commerce/commerce_portlet_settings.json")))));

		return portletPreferenceValueModels;
	}

	public CommerceOrderItemModel newCommerceOrderItemModel(
		CommerceOrderModel commerceOrderModel, long commercePriceListId,
		CProductModel cProductModel) {

		CommerceOrderItemModel commerceOrderItemModel =
			new CommerceOrderItemModelImpl();

		// PK fields

		commerceOrderItemModel.setCommerceOrderItemId(_counter.get());

		// Group instance

		commerceOrderItemModel.setGroupId(commerceOrderModel.getGroupId());

		// Audit fields

		commerceOrderItemModel.setCompanyId(_companyId);
		commerceOrderItemModel.setUserId(_sampleUserId);
		commerceOrderItemModel.setUserName(_SAMPLE_USER_NAME);
		commerceOrderItemModel.setCreateDate(new Date());
		commerceOrderItemModel.setModifiedDate(new Date());

		// Other fields

		commerceOrderItemModel.setCommerceInventoryBookedQuantityId(0);
		commerceOrderItemModel.setCommerceOrderId(
			commerceOrderModel.getCommerceOrderId());
		commerceOrderItemModel.setCommercePriceListId(commercePriceListId);
		commerceOrderItemModel.setCProductId(cProductModel.getCProductId());

		CPInstanceModel cpInstanceModel = _cpInstanceModels.get(
			cProductModel.getPublishedCPDefinitionId());

		commerceOrderItemModel.setCPInstanceId(
			cpInstanceModel.getCPInstanceId());

		commerceOrderItemModel.setParentCommerceOrderItemId(0);
		commerceOrderItemModel.setName("Commerce Order Item Name");
		commerceOrderItemModel.setQuantity(BigDecimal.ONE);
		commerceOrderItemModel.setShippedQuantity(BigDecimal.ONE);
		commerceOrderItemModel.setShipSeparately(true);
		commerceOrderItemModel.setShippable(true);
		commerceOrderItemModel.setDiscountAmount(BigDecimal.valueOf(0));
		commerceOrderItemModel.setDiscountPercentageLevel1(
			BigDecimal.valueOf(0));
		commerceOrderItemModel.setDiscountPercentageLevel2(
			BigDecimal.valueOf(0));
		commerceOrderItemModel.setDiscountPercentageLevel3(
			BigDecimal.valueOf(0));
		commerceOrderItemModel.setDiscountPercentageLevel4(
			BigDecimal.valueOf(0));
		commerceOrderItemModel.setDiscountPercentageLevel1WithTaxAmount(
			BigDecimal.valueOf(0));
		commerceOrderItemModel.setDiscountPercentageLevel2WithTaxAmount(
			BigDecimal.valueOf(0));
		commerceOrderItemModel.setDiscountPercentageLevel3WithTaxAmount(
			BigDecimal.valueOf(0));
		commerceOrderItemModel.setDiscountPercentageLevel4WithTaxAmount(
			BigDecimal.valueOf(0));
		commerceOrderItemModel.setDiscountWithTaxAmount(BigDecimal.valueOf(0));
		commerceOrderItemModel.setFinalPrice(BigDecimal.valueOf(0));
		commerceOrderItemModel.setFinalPriceWithTaxAmount(
			BigDecimal.valueOf(0));
		commerceOrderItemModel.setJson("[]");
		commerceOrderItemModel.setPromoPrice(BigDecimal.valueOf(0));
		commerceOrderItemModel.setPromoPriceWithTaxAmount(
			BigDecimal.valueOf(0));
		commerceOrderItemModel.setUnitPrice(BigDecimal.valueOf(0));
		commerceOrderItemModel.setUnitPriceWithTaxAmount(BigDecimal.valueOf(0));
		commerceOrderItemModel.setSku(cpInstanceModel.getSku());
		commerceOrderItemModel.setDeliveryGroupName(null);
		commerceOrderItemModel.setShippingAddressId(0);
		commerceOrderItemModel.setPrintedNote(null);
		commerceOrderItemModel.setRequestedDeliveryDate(null);
		commerceOrderItemModel.setManuallyAdjusted(false);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		commerceOrderItemModel.setUuid(uuid);
		commerceOrderItemModel.setExternalReferenceCode(uuid);

		return commerceOrderItemModel;
	}

	public CommerceOrderModel newCommerceOrderModel(
		long groupId, long commerceAccountId, long commerceBillingAddressId,
		String commerceCurrencyCode, int commerceOrderStatus,
		long commerceShippingAddressId, long commerceShippingMethodId,
		String commerceShippingOptionName) {

		CommerceOrderModel commerceOrderModel = new CommerceOrderModelImpl();

		// PK fields

		commerceOrderModel.setCommerceOrderId(_counter.get());

		// Group instance

		commerceOrderModel.setGroupId(groupId);

		// Audit fields

		commerceOrderModel.setCompanyId(_companyId);
		commerceOrderModel.setUserId(_sampleUserId);
		commerceOrderModel.setUserName(_SAMPLE_USER_NAME);
		commerceOrderModel.setCreateDate(new Date());
		commerceOrderModel.setModifiedDate(new Date());

		// Other fields

		commerceOrderModel.setCommerceAccountId(commerceAccountId);
		commerceOrderModel.setCommerceCurrencyCode(commerceCurrencyCode);
		commerceOrderModel.setBillingAddressId(commerceBillingAddressId);
		commerceOrderModel.setShippingAddressId(commerceShippingAddressId);
		commerceOrderModel.setCommerceShippingMethodId(
			commerceShippingMethodId);
		commerceOrderModel.setShippingOptionName(commerceShippingOptionName);
		commerceOrderModel.setShippingAddressId(commerceShippingAddressId);
		commerceOrderModel.setSubtotal(BigDecimal.valueOf(0));
		commerceOrderModel.setShippingAmount(BigDecimal.valueOf(0));
		commerceOrderModel.setTotal(BigDecimal.valueOf(0));
		commerceOrderModel.setSubtotalWithTaxAmount(BigDecimal.valueOf(0));
		commerceOrderModel.setShippingWithTaxAmount(BigDecimal.valueOf(0));
		commerceOrderModel.setTotalWithTaxAmount(BigDecimal.valueOf(0));
		commerceOrderModel.setPaymentStatus(1);
		commerceOrderModel.setOrderDate(null);
		commerceOrderModel.setOrderStatus(commerceOrderStatus);
		commerceOrderModel.setPrintedNote(null);
		commerceOrderModel.setRequestedDeliveryDate(null);
		commerceOrderModel.setStatus(0);
		commerceOrderModel.setStatusByUserId(_sampleUserId);
		commerceOrderModel.setStatusByUserName(_SAMPLE_USER_NAME);
		commerceOrderModel.setStatusDate(new Date());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		commerceOrderModel.setUuid(uuid);
		commerceOrderModel.setExternalReferenceCode(uuid);

		return commerceOrderModel;
	}

	public List<CommerceOrderModel> newCommerceOrderModels(
		long groupId, long accountEntryId, long commerceBillingAddressId,
		String commerceCurrencyCode, int commerceOrderStatus,
		long commerceShippingAddressId, long commerceShippingMethodId,
		String commerceShippingOptionName) {

		int maxCommerceOrderCount = 0;

		if (commerceOrderStatus ==
				CommerceOrderConstants.ORDER_STATUS_CANCELLED) {

			maxCommerceOrderCount =
				BenchmarksPropsValues.MAX_COMMERCE_ORDER_STATUS_CANCELLED_COUNT;

			if (BenchmarksPropsValues.MAX_COMMERCE_GROUP_COUNT > 1) {
				maxCommerceOrderCount =
					maxCommerceOrderCount /
						BenchmarksPropsValues.MAX_COMMERCE_GROUP_COUNT;
			}
		}
		else if (commerceOrderStatus ==
					CommerceOrderConstants.ORDER_STATUS_PENDING) {

			maxCommerceOrderCount =
				BenchmarksPropsValues.MAX_COMMERCE_ORDER_STATUS_PENDING_COUNT;

			if (BenchmarksPropsValues.MAX_COMMERCE_GROUP_COUNT > 1) {
				maxCommerceOrderCount =
					maxCommerceOrderCount /
						BenchmarksPropsValues.MAX_COMMERCE_GROUP_COUNT;
			}
		}
		else if (commerceOrderStatus ==
					CommerceOrderConstants.ORDER_STATUS_OPEN) {

			maxCommerceOrderCount =
				BenchmarksPropsValues.MAX_COMMERCE_ORDER_STATUS_OPEN_COUNT;
		}

		List<CommerceOrderModel> commerceOrderModels = new ArrayList<>(
			maxCommerceOrderCount);

		for (int i = 1; i <= maxCommerceOrderCount; i++) {
			commerceOrderModels.add(
				newCommerceOrderModel(
					groupId, accountEntryId, commerceBillingAddressId,
					commerceCurrencyCode, commerceOrderStatus,
					commerceShippingAddressId, commerceShippingMethodId,
					commerceShippingOptionName));
		}

		return commerceOrderModels;
	}

	public List<CommerceOrderModel> newCommerceOrderModels(
		long groupId, long accountEntryId, String commerceCurrencyCode,
		int commerceOrderStatus, long commerceShippingMethodId) {

		return newCommerceOrderModels(
			groupId, accountEntryId, _firstAddressModel.getAddressId(),
			commerceCurrencyCode, commerceOrderStatus,
			_firstAddressModel.getAddressId(), commerceShippingMethodId,
			"Standard Delivery");
	}

	public List<PortletPreferencesModel> newCommercePortletPreferencesModels(
			LayoutModel layoutModel)
		throws IOException {

		List<PortletPreferencesModel> portletPreferencesModels =
			new ArrayList<>();

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.create(
				true
			).load(
				StringUtil.replace(
					layoutModel.getTypeSettings(), "\\n", StringPool.NEW_LINE)
			).build();

		Set<String> typeSettingPropertiesKeys =
			typeSettingsUnicodeProperties.keySet();

		for (String typeSettingPropertiesKey : typeSettingPropertiesKeys) {
			if (typeSettingPropertiesKey.startsWith("column-")) {
				String[] portletIds = StringUtil.split(
					typeSettingsUnicodeProperties.getProperty(
						typeSettingPropertiesKey));

				for (String portletId : portletIds) {
					portletPreferencesModels.add(
						newPortletPreferencesModel(
							layoutModel.getPlid(), portletId));
				}
			}
		}

		return portletPreferencesModels;
	}

	public CommercePriceEntryModel newCommercePriceEntryModel(
		long commercePriceListId, String cpInstanceUuid, long cProductId) {

		CommercePriceEntryModel commercePriceEntryModel =
			new CommercePriceEntryModelImpl();

		// PK fields

		commercePriceEntryModel.setCommercePriceEntryId(_counter.get());

		// Audit fields

		commercePriceEntryModel.setCompanyId(_companyId);
		commercePriceEntryModel.setUserId(_sampleUserId);
		commercePriceEntryModel.setUserName(_SAMPLE_USER_NAME);
		commercePriceEntryModel.setCreateDate(new Date());
		commercePriceEntryModel.setModifiedDate(new Date());

		// Other fields

		commercePriceEntryModel.setCommercePriceListId(commercePriceListId);
		commercePriceEntryModel.setCPInstanceUuid(cpInstanceUuid);
		commercePriceEntryModel.setCProductId(cProductId);
		commercePriceEntryModel.setPrice(BigDecimal.valueOf(0));
		commercePriceEntryModel.setPromoPrice(BigDecimal.valueOf(0));
		commercePriceEntryModel.setDiscountDiscovery(true);
		commercePriceEntryModel.setDisplayDate(new Date());
		commercePriceEntryModel.setStatus(0);
		commercePriceEntryModel.setStatusByUserId(_sampleUserId);
		commercePriceEntryModel.setStatusByUserName(_SAMPLE_USER_NAME);
		commercePriceEntryModel.setStatusDate(new Date());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		commercePriceEntryModel.setUuid(uuid);
		commercePriceEntryModel.setExternalReferenceCode(uuid);

		return commercePriceEntryModel;
	}

	public CommercePriceListModel newCommercePriceListModel(
		long groupId, String commerceCurrencyCode, boolean catalogBasePriceList,
		boolean netPrice, String type) {

		CommercePriceListModel commercePriceListModel =
			new CommercePriceListModelImpl();

		// PK fields

		commercePriceListModel.setCommercePriceListId(_counter.get());

		// Group instance

		commercePriceListModel.setGroupId(groupId);

		// Audit fields

		commercePriceListModel.setCompanyId(_companyId);
		commercePriceListModel.setUserId(_sampleUserId);
		commercePriceListModel.setUserName(_SAMPLE_USER_NAME);
		commercePriceListModel.setCreateDate(new Date());
		commercePriceListModel.setModifiedDate(new Date());

		// Other fields

		commercePriceListModel.setCommerceCurrencyCode(commerceCurrencyCode);
		commercePriceListModel.setParentCommercePriceListId(0);
		commercePriceListModel.setCatalogBasePriceList(catalogBasePriceList);
		commercePriceListModel.setNetPrice(netPrice);
		commercePriceListModel.setType(type);
		commercePriceListModel.setName("Price List");
		commercePriceListModel.setPriority(0);
		commercePriceListModel.setDisplayDate(new Date());
		commercePriceListModel.setExpirationDate(null);
		commercePriceListModel.setLastPublishDate(new Date());
		commercePriceListModel.setStatus(0);
		commercePriceListModel.setStatusByUserId(_sampleUserId);
		commercePriceListModel.setStatusByUserName(_SAMPLE_USER_NAME);
		commercePriceListModel.setStatusDate(new Date());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		commercePriceListModel.setUuid(uuid);
		commercePriceListModel.setExternalReferenceCode(uuid);

		return commercePriceListModel;
	}

	public List<CommercePriceListModel> newCommercePriceListModels(
		long groupId, String commerceCurrencyCode, boolean catalogBasePriceList,
		boolean netPrice, String type) {

		List<CommercePriceListModel> commercePriceListModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_COMMERCE_PRICE_LIST_COUNT);

		for (int i = 1;
			 i <= BenchmarksPropsValues.MAX_COMMERCE_PRICE_LIST_COUNT; i++) {

			commercePriceListModels.add(
				newCommercePriceListModel(
					groupId, commerceCurrencyCode, catalogBasePriceList,
					netPrice, type));
		}

		return commercePriceListModels;
	}

	public CommerceShippingFixedOptionModel newCommerceShippingFixedOptionModel(
		long groupId, long commerceShippingMethodId) {

		CommerceShippingFixedOptionModel commerceShippingFixedOptionModel =
			new CommerceShippingFixedOptionModelImpl();

		// PK fields

		long commerceShippingFixedOptionId = _counter.get();

		commerceShippingFixedOptionModel.setCommerceShippingFixedOptionId(
			commerceShippingFixedOptionId);

		// Group instance

		commerceShippingFixedOptionModel.setGroupId(groupId);

		// Audit fields

		commerceShippingFixedOptionModel.setCompanyId(_companyId);
		commerceShippingFixedOptionModel.setUserId(_sampleUserId);
		commerceShippingFixedOptionModel.setUserName(_SAMPLE_USER_NAME);
		commerceShippingFixedOptionModel.setCreateDate(new Date());
		commerceShippingFixedOptionModel.setModifiedDate(new Date());

		// Other fields

		commerceShippingFixedOptionModel.setCommerceShippingMethodId(
			commerceShippingMethodId);
		commerceShippingFixedOptionModel.setName(
			StringBundler.concat(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root ",
				"available-locales=\"en_US\" default-locale=\"en_US\"><Name ",
				"language-id=\"en_US\">Standard Delivery</Name></root>"));
		commerceShippingFixedOptionModel.setDescription(null);
		commerceShippingFixedOptionModel.setAmount(BigDecimal.valueOf(15));
		commerceShippingFixedOptionModel.setKey(
			StringUtil.randomString(3) + commerceShippingFixedOptionId);
		commerceShippingFixedOptionModel.setPriority(0);

		return commerceShippingFixedOptionModel;
	}

	public CommerceShippingMethodModel newCommerceShippingMethodModel(
		long groupId) {

		CommerceShippingMethodModel commerceShippingMethodModel =
			new CommerceShippingMethodModelImpl();

		// PK fields

		commerceShippingMethodModel.setCommerceShippingMethodId(_counter.get());

		// Group instance

		commerceShippingMethodModel.setGroupId(groupId);

		// Audit fields

		commerceShippingMethodModel.setCompanyId(_companyId);
		commerceShippingMethodModel.setUserId(_sampleUserId);
		commerceShippingMethodModel.setUserName(_SAMPLE_USER_NAME);
		commerceShippingMethodModel.setCreateDate(new Date());
		commerceShippingMethodModel.setModifiedDate(new Date());

		// Other fields

		commerceShippingMethodModel.setName(
			StringBundler.concat(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root ",
				"available-locales=\"en_US\" default-locale=\"en_US\"><Name ",
				"language-id=\"en_US\">Flat Rate</Name></root>"));
		commerceShippingMethodModel.setDescription(
			StringBundler.concat(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root ",
				"available-locales=\"en_US\" default-locale=\"en_US\">",
				"<Description language-id=\"en_US\">Ship for a fixed price",
				"</Description></root>"));
		commerceShippingMethodModel.setImageId(0);
		commerceShippingMethodModel.setEngineKey("fixed");
		commerceShippingMethodModel.setPriority(0);
		commerceShippingMethodModel.setActive(true);

		return commerceShippingMethodModel;
	}

	public List<CommerceShippingMethodModel> newCommerceShippingMethodModels(
		List<GroupModel> groupModels) {

		return TransformUtil.transform(
			groupModels,
			groupModel -> newCommerceShippingMethodModel(
				groupModel.getGroupId()));
	}

	public List<DDMTemplateModel>
			newCommerceSiteNavigationPortletDDMTemplateModels(long groupId)
		throws Exception {

		List<DDMTemplateModel> ddmTemplateModels = new ArrayList<>();

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			StringUtil.read(
				getResourceInputStream(
					"commerce/commerce_theme_portlet_settings.json")));

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			JSONObject portletPreferencesJSONObject = jsonObject.getJSONObject(
				"portletPreferences");

			JSONObject displayStyleJSONObject =
				portletPreferencesJSONObject.getJSONObject("displayStyle");

			String fileName =
				"commerce/" + displayStyleJSONObject.getString("FileName");

			ddmTemplateModels.add(
				newDDMTemplateModel(
					groupId, _sampleUserId,
					DDMTemplateConstants.TEMPLATE_MODE_CREATE,
					displayStyleJSONObject.getString("Name"),
					StringUtil.read(getResourceInputStream(fileName)),
					getClassNameId(NavItem.class), 0,
					getClassNameId(PortletDisplayTemplate.class),
					_counter.get(),
					StringUtil.removeSubstring(fileName, ".ftl")));
		}

		return ddmTemplateModels;
	}

	public List<PortletPreferencesModel>
			newCommerceSiteNavigationPortletPreferencesModels(
				GroupModel groupModel)
		throws Exception {

		List<PortletPreferencesModel> portletPreferencesModels =
			new ArrayList<>();

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			StringUtil.read(
				getResourceInputStream(
					"commerce/commerce_theme_portlet_settings.json")));

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			portletPreferencesModels.add(
				newPortletPreferencesModel(
					groupModel.getGroupId(),
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT, 0,
					jsonObject.getString("portletName") + "_INSTANCE_" +
						jsonObject.getString("instanceId")));
		}

		return portletPreferencesModels;
	}

	public List<PortletPreferenceValueModel>
			newCommerceSiteNavigationPortletPreferenceValueModels(
				List<PortletPreferencesModel> portletPreferencesModels)
		throws Exception {

		return newCommercePortletPreferenceValueModels(
			portletPreferencesModels,
			JSONFactoryUtil.createJSONArray(
				StringUtil.read(
					getResourceInputStream(
						"commerce/commerce_theme_portlet_settings.json"))));
	}

	public List<CompanyModel> newCompanyModels() {
		List<CompanyModel> companyModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_COMPANY_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_COMPANY_COUNT; i++) {
			companyModels.add(
				_newCompanyModel(
					StringBundler.concat(
						BenchmarksPropsValues.VIRTUAL_HOSTNAME_PREFIX, i,
						".com")));
		}

		return companyModels;
	}

	public ContactModel newContactModel(UserModel userModel) {
		ContactModel contactModel = new ContactModelImpl();

		// PK fields

		contactModel.setContactId(userModel.getContactId());

		// Audit fields

		contactModel.setCompanyId(userModel.getCompanyId());
		contactModel.setUserId(userModel.getUserId());

		FullNameGenerator fullNameGenerator =
			FullNameGeneratorFactory.getInstance();

		contactModel.setUserName(
			fullNameGenerator.getFullName(
				userModel.getFirstName(), userModel.getMiddleName(),
				userModel.getLastName()));

		contactModel.setCreateDate(new Date());
		contactModel.setModifiedDate(new Date());

		// Other fields

		contactModel.setClassNameId(getClassNameId(User.class));
		contactModel.setClassPK(userModel.getUserId());
		contactModel.setParentContactId(
			ContactConstants.DEFAULT_PARENT_CONTACT_ID);
		contactModel.setEmailAddress(userModel.getEmailAddress());
		contactModel.setFirstName(userModel.getFirstName());
		contactModel.setLastName(userModel.getLastName());
		contactModel.setMale(true);
		contactModel.setBirthday(new Date());

		return contactModel;
	}

	public LayoutModel newContentLayoutModel(
		long groupId, String name, String fragmentEntries) {

		SimpleCounter simpleCounter = _layoutIdCounters.computeIfAbsent(
			LayoutLocalServiceImpl.getCounterName(groupId, false),
			counterName -> new SimpleCounter());

		LayoutModel layoutModel = new LayoutModelImpl();

		// PK fields

		layoutModel.setPlid(_layoutPlidCounter.get());

		// Group instance

		layoutModel.setGroupId(groupId);

		// Audit fields

		layoutModel.setCompanyId(_companyId);
		layoutModel.setUserId(_sampleUserId);
		layoutModel.setUserName(_SAMPLE_USER_NAME);
		layoutModel.setCreateDate(new Date());
		layoutModel.setModifiedDate(new Date());

		// Other fields

		layoutModel.setLayoutId(simpleCounter.get());
		layoutModel.setName(
			"<?xml version=\"1.0\"?><root><name>" + name + "</name></root>");
		layoutModel.setType(LayoutConstants.TYPE_CONTENT);
		layoutModel.setTypeSettings(
			StringUtil.replace(
				UnicodePropertiesBuilder.create(
					true
				).put(
					"fragmentEntries", fragmentEntries
				).buildString(),
				'\n', "\\n"));
		layoutModel.setFriendlyURL(StringPool.FORWARD_SLASH + name);
		layoutModel.setLastPublishDate(new Date());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		layoutModel.setUuid(uuid);
		layoutModel.setExternalReferenceCode(uuid);

		return layoutModel;
	}

	public List<LayoutModel> newContentLayoutModels(long groupId) {
		List<LayoutModel> layoutModels = new ArrayList<>();

		for (int i = 0; i < BenchmarksPropsValues.MAX_CONTENT_LAYOUT_COUNT;
			 i++) {

			layoutModels.add(
				newContentLayoutModel(
					groupId, i + "_web_content", "web_content"));
		}

		return layoutModels;
	}

	public List<LayoutModel> newContentPageLayoutModels(
		long groupId, String name) {

		List<LayoutModel> layoutModels = new ArrayList<>();

		LayoutModel publicLayoutModel = _newContentPageLayoutModel(
			groupId, 0, 0, name, name);

		layoutModels.add(publicLayoutModel);
		layoutModels.add(
			_newContentPageLayoutModel(
				groupId, getClassNameId(Layout.class),
				publicLayoutModel.getPlid(), name + "1",
				String.valueOf(
					new UUID(
						SecureRandomUtil.nextLong(),
						SecureRandomUtil.nextLong()))));

		return layoutModels;
	}

	public List<CounterModel> newCounterModels() {
		List<CounterModel> counterModels = new ArrayList<>();

		counterModels.add(
			_newCounterModel(Counter.class.getName(), _counter.get()));
		counterModels.add(
			_newCounterModel(DDMField.class.getName(), _counter.get()));
		counterModels.add(
			_newCounterModel(
				DDMFieldAttribute.class.getName(), _counter.get()));
		counterModels.add(
			_newCounterModel(
				DLFileEntry.class.getName(), _dlFileEntryIdCounter.get()));
		counterModels.add(
			_newCounterModel(
				FriendlyURLEntryLocalization.class.getName(), _counter.get()));
		counterModels.add(
			_newCounterModel(
				PortletPreferenceValue.class.getName(),
				_portletPreferenceValueIdCounter.get()));
		counterModels.add(
			_newCounterModel(
				ResourcePermission.class.getName(),
				_resourcePermissionIdCounter.get()));
		counterModels.add(
			_newCounterModel(
				SocialActivity.class.getName(),
				_socialActivityIdCounter.get()));

		for (Map.Entry<String, SimpleCounter> entry :
				_layoutIdCounters.entrySet()) {

			SimpleCounter simpleCounter = entry.getValue();

			counterModels.add(
				_newCounterModel(entry.getKey(), simpleCounter.get()));
		}

		counterModels.add(
			_newCounterModel(Layout.class.getName(), _layoutPlidCounter.get()));

		counterModels.add(
			_newCounterModel(
				LayoutSet.class.getName(), _layoutSetIdCounter.get()));

		return counterModels;
	}

	public CountryModel newCountryModel() {
		CountryModel countryModel = new CountryModelImpl();

		// PK fields

		countryModel.setCountryId(_counter.get());

		// Audit fields

		countryModel.setCompanyId(_companyId);
		countryModel.setUserId(_sampleUserId);
		countryModel.setUserName(_SAMPLE_USER_NAME);
		countryModel.setCreateDate(new Date());
		countryModel.setModifiedDate(new Date());

		// Other fields

		countryModel.setA2("SP");
		countryModel.setA3("SAM");
		countryModel.setActive(true);
		countryModel.setBillingAllowed(true);
		countryModel.setIdd("0");
		countryModel.setName("sample-country");
		countryModel.setNumber("0");
		countryModel.setShippingAllowed(true);

		// Autogenerated fields

		countryModel.setUuid(SequentialUUID.generate());

		return countryModel;
	}

	public CPAttachmentFileEntryModel newCPAttachmentFileEntryModel(
		long groupId, long cpDefinitionId, int index, int type) {

		CPAttachmentFileEntryModel cpAttachmentFileEntryModel =
			new CPAttachmentFileEntryModelImpl();

		// PK fields

		cpAttachmentFileEntryModel.setCPAttachmentFileEntryId(_counter.get());

		// Group instance

		cpAttachmentFileEntryModel.setGroupId(groupId);

		// Audit fields

		cpAttachmentFileEntryModel.setCompanyId(_companyId);
		cpAttachmentFileEntryModel.setUserId(_sampleUserId);
		cpAttachmentFileEntryModel.setUserName(_SAMPLE_USER_NAME);
		cpAttachmentFileEntryModel.setCreateDate(new Date());
		cpAttachmentFileEntryModel.setModifiedDate(new Date());

		// Other fields

		cpAttachmentFileEntryModel.setClassNameId(
			getClassNameId(CPDefinition.class));
		cpAttachmentFileEntryModel.setClassPK(cpDefinitionId);
		cpAttachmentFileEntryModel.setFileEntryId(_counter.get());
		cpAttachmentFileEntryModel.setDisplayDate(null);
		cpAttachmentFileEntryModel.setExpirationDate(null);
		cpAttachmentFileEntryModel.setTitle(
			StringBundler.concat(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root ",
				"available-locales=\"en_US\" default-locale=\"en_US\"><Title ",
				"language-id=\"en_US\">Attachment file Entry  ", index,
				"</Title></root>"));
		cpAttachmentFileEntryModel.setPriority(0);
		cpAttachmentFileEntryModel.setType(type);
		cpAttachmentFileEntryModel.setLastPublishDate(new Date());
		cpAttachmentFileEntryModel.setStatus(0);
		cpAttachmentFileEntryModel.setStatusByUserId(_sampleUserId);
		cpAttachmentFileEntryModel.setStatusByUserName(_SAMPLE_USER_NAME);
		cpAttachmentFileEntryModel.setStatusDate(new Date());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		cpAttachmentFileEntryModel.setUuid(uuid);
		cpAttachmentFileEntryModel.setExternalReferenceCode(uuid);

		return cpAttachmentFileEntryModel;
	}

	public CPDefinitionLocalizationModel newCPDefinitionLocalizationModel(
		CPDefinitionModel cpDefinitionModel) {

		CPDefinitionLocalizationModel cpDefinitionLocalizationModel =
			new CPDefinitionLocalizationModelImpl();

		// Localized entity

		long cpDefinitionId = cpDefinitionModel.getCPDefinitionId();

		cpDefinitionLocalizationModel.setName("Definition " + cpDefinitionId);
		cpDefinitionLocalizationModel.setShortDescription(
			"Short description for definition " + cpDefinitionId);
		cpDefinitionLocalizationModel.setDescription(
			"A longer and more verbose description for definition with ID " +
				cpDefinitionId);
		cpDefinitionLocalizationModel.setMetaTitle(
			"A meta-title for definition " + cpDefinitionId);
		cpDefinitionLocalizationModel.setMetaDescription(
			"A meta-description for definition " + cpDefinitionId);
		cpDefinitionLocalizationModel.setMetaKeywords(
			"Meta-keywords for definition " + cpDefinitionId);

		// Autogenerated fields

		cpDefinitionLocalizationModel.setCompanyId(_companyId);
		cpDefinitionLocalizationModel.setCPDefinitionId(cpDefinitionId);
		cpDefinitionLocalizationModel.setCpDefinitionLocalizationId(
			_counter.get());
		cpDefinitionLocalizationModel.setLanguageId("en_US");

		return cpDefinitionLocalizationModel;
	}

	public CPDefinitionModel newCPDefinitionModel(
		CPTaxCategoryModel cpTaxCategoryModel, CProductModel cProductModel,
		int version) {

		CPDefinitionModel cpDefinitionModel = new CPDefinitionModelImpl();

		// PK fields

		if (version ==
				BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_DEFINITION_COUNT) {

			cpDefinitionModel.setCPDefinitionId(
				cProductModel.getPublishedCPDefinitionId());
		}
		else {
			cpDefinitionModel.setCPDefinitionId(_counter.get());
		}

		// Group instance

		cpDefinitionModel.setGroupId(cProductModel.getGroupId());

		// Audit fields

		cpDefinitionModel.setCompanyId(_companyId);
		cpDefinitionModel.setUserId(_sampleUserId);
		cpDefinitionModel.setUserName(_SAMPLE_USER_NAME);
		cpDefinitionModel.setCreateDate(new Date());
		cpDefinitionModel.setModifiedDate(new Date());

		// Other fields

		cpDefinitionModel.setCProductId(cProductModel.getCProductId());
		cpDefinitionModel.setCPTaxCategoryId(
			cpTaxCategoryModel.getCPTaxCategoryId());
		cpDefinitionModel.setProductTypeName("simple");
		cpDefinitionModel.setAvailableIndividually(true);
		cpDefinitionModel.setIgnoreSKUCombinations(true);
		cpDefinitionModel.setShippable(true);
		cpDefinitionModel.setFreeShipping(false);
		cpDefinitionModel.setShipSeparately(true);
		cpDefinitionModel.setShippingExtraPrice(3.0);
		cpDefinitionModel.setWidth(0);
		cpDefinitionModel.setHeight(0);
		cpDefinitionModel.setDepth(0);
		cpDefinitionModel.setWeight(0);
		cpDefinitionModel.setTaxExempt(false);
		cpDefinitionModel.setTelcoOrElectronics(false);
		cpDefinitionModel.setDDMStructureKey(null);
		cpDefinitionModel.setPublished(true);
		cpDefinitionModel.setDisplayDate(new Date());
		cpDefinitionModel.setExpirationDate(null);
		cpDefinitionModel.setLastPublishDate(null);
		cpDefinitionModel.setSubscriptionEnabled(false);
		cpDefinitionModel.setSubscriptionLength(0);
		cpDefinitionModel.setSubscriptionType(null);
		cpDefinitionModel.setSubscriptionTypeSettings(null);
		cpDefinitionModel.setMaxSubscriptionCycles(0);
		cpDefinitionModel.setChannelFilterEnabled(true);
		cpDefinitionModel.setVersion(version);
		cpDefinitionModel.setStatus(WorkflowConstants.STATUS_APPROVED);
		cpDefinitionModel.setStatusByUserId(_sampleUserId);
		cpDefinitionModel.setStatusByUserName(_SAMPLE_USER_NAME);
		cpDefinitionModel.setStatusDate(new Date());

		// Autogenerated fields

		cpDefinitionModel.setUuid(SequentialUUID.generate());

		return cpDefinitionModel;
	}

	public AssetEntryModel newCPDefinitionModelAssetEntryModel(
		CPDefinitionModel cpDefinitionModel, long groupId) {

		return newAssetEntryModel(
			groupId, new Date(), new Date(), getClassNameId(CPDefinition.class),
			cpDefinitionModel.getCPDefinitionId(), SequentialUUID.generate(), 0,
			true, true, "text/plain",
			"Definition " + cpDefinitionModel.getCPDefinitionId());
	}

	public List<CPDefinitionModel> newCPDefinitionModels(
		CPTaxCategoryModel cpTaxCategoryModel, CProductModel cProductModel) {

		List<CPDefinitionModel> cPDefinitionModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_DEFINITION_COUNT);

		for (int i = 1;
			 i <= BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_DEFINITION_COUNT;
			 i++) {

			cPDefinitionModels.add(
				newCPDefinitionModel(cpTaxCategoryModel, cProductModel, i));
		}

		return cPDefinitionModels;
	}

	public CPDefinitionSpecificationOptionValueModel
		newCPDefinitionSpecificationOptionValueModel(
			long cpDefinitionId, long cpSpecificationOptionId,
			long cpOptionCategoryId, int index) {

		CPDefinitionSpecificationOptionValueModel
			cpDefinitionSpecificationOptionValueModel =
				new CPDefinitionSpecificationOptionValueModelImpl();

		// PK fields

		cpDefinitionSpecificationOptionValueModel.
			setCPDefinitionSpecificationOptionValueId(_counter.get());

		// Audit fields

		cpDefinitionSpecificationOptionValueModel.setCompanyId(_companyId);
		cpDefinitionSpecificationOptionValueModel.setUserId(_sampleUserId);
		cpDefinitionSpecificationOptionValueModel.setUserName(
			_SAMPLE_USER_NAME);
		cpDefinitionSpecificationOptionValueModel.setCreateDate(new Date());
		cpDefinitionSpecificationOptionValueModel.setModifiedDate(new Date());

		// Other fields

		cpDefinitionSpecificationOptionValueModel.setCPDefinitionId(
			cpDefinitionId);
		cpDefinitionSpecificationOptionValueModel.setCPSpecificationOptionId(
			cpSpecificationOptionId);
		cpDefinitionSpecificationOptionValueModel.setCPOptionCategoryId(
			cpOptionCategoryId);
		cpDefinitionSpecificationOptionValueModel.setValue(
			StringBundler.concat(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root ",
				"available-locales=\"en_US\" default-locale=\"en_US\"><Value ",
				"language-id=\"en_US\">Specification Option Value ", index,
				"</Value></root>"));
		cpDefinitionSpecificationOptionValueModel.setPriority(index - 1);
		cpDefinitionSpecificationOptionValueModel.setLastPublishDate(null);

		// Autogenerated fields

		cpDefinitionSpecificationOptionValueModel.setUuid(
			SequentialUUID.generate());

		return cpDefinitionSpecificationOptionValueModel;
	}

	public List<CPDefinitionSpecificationOptionValueModel>
		newCPDefinitionSpecificationOptionValueModels(
			long cpDefinitionId, long cpSpecificationOptionId,
			long cpOptionCategoryId, int index) {

		List<CPDefinitionSpecificationOptionValueModel>
			cpDefinitionSpecificationOptionValueModels = new ArrayList<>(
				BenchmarksPropsValues.
					MAX_CP_DEFINITION_SPECIFICATION_OPTION_VALUE_COUNT);

		for (int i = 1;
			 i <=
				 BenchmarksPropsValues.
					 MAX_CP_DEFINITION_SPECIFICATION_OPTION_VALUE_COUNT;
			 i++) {

			cpDefinitionSpecificationOptionValueModels.add(
				newCPDefinitionSpecificationOptionValueModel(
					cpDefinitionId, cpSpecificationOptionId, cpOptionCategoryId,
					i));
		}

		return cpDefinitionSpecificationOptionValueModels;
	}

	public CPInstanceModel newCPInstanceModel(
		CPDefinitionModel cpDefinitionModel, int index) {

		CPInstanceModel cpInstanceModel = new CPInstanceModelImpl();

		// PK fields

		cpInstanceModel.setCPInstanceId(_counter.get());

		// Group instance

		cpInstanceModel.setGroupId(cpDefinitionModel.getGroupId());

		// Audit fields

		cpInstanceModel.setCompanyId(_companyId);
		cpInstanceModel.setUserId(_sampleUserId);
		cpInstanceModel.setUserName(_SAMPLE_USER_NAME);
		cpInstanceModel.setCreateDate(new Date());
		cpInstanceModel.setModifiedDate(new Date());

		// Other fields

		long cpDefinitionId = cpDefinitionModel.getCPDefinitionId();

		cpInstanceModel.setCPDefinitionId(cpDefinitionId);

		cpInstanceModel.setCPInstanceUuid(SequentialUUID.generate());

		String instanceKey = cpDefinitionId + StringPool.POUND + index;

		cpInstanceModel.setSku("SKU" + instanceKey);
		cpInstanceModel.setGtin("GTIN" + instanceKey);
		cpInstanceModel.setManufacturerPartNumber("MPN" + instanceKey);

		cpInstanceModel.setPurchasable(true);
		cpInstanceModel.setWidth((index * 2) + 1);
		cpInstanceModel.setHeight(index + 5);
		cpInstanceModel.setDepth(index);
		cpInstanceModel.setWeight((index * 3) + 1);
		cpInstanceModel.setPrice(BigDecimal.valueOf(index + 10.1));
		cpInstanceModel.setPromoPrice(BigDecimal.valueOf(index + 9.2));
		cpInstanceModel.setCost(BigDecimal.valueOf(index + 6.4));
		cpInstanceModel.setPublished(true);
		cpInstanceModel.setDisplayDate(new Date());
		cpInstanceModel.setExpirationDate(null);
		cpInstanceModel.setLastPublishDate(null);
		cpInstanceModel.setOverrideSubscriptionInfo(false);
		cpInstanceModel.setSubscriptionEnabled(false);
		cpInstanceModel.setSubscriptionLength(0);
		cpInstanceModel.setSubscriptionType(null);
		cpInstanceModel.setSubscriptionTypeSettings(null);
		cpInstanceModel.setMaxSubscriptionCycles(0);
		cpInstanceModel.setStatus(WorkflowConstants.STATUS_APPROVED);
		cpInstanceModel.setStatusByUserId(_sampleUserId);
		cpInstanceModel.setStatusByUserName(_SAMPLE_USER_NAME);
		cpInstanceModel.setStatusDate(new Date());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		cpInstanceModel.setUuid(uuid);
		cpInstanceModel.setExternalReferenceCode(uuid);

		_cpInstanceModels.put(cpDefinitionId, cpInstanceModel);

		return cpInstanceModel;
	}

	public List<CPInstanceModel> newCPInstanceModels(
		CPDefinitionModel cpDefinitionModel) {

		List<CPInstanceModel> cPInstanceModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_INSTANCE_COUNT);

		for (int i = 1;
			 i <= BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_INSTANCE_COUNT;
			 i++) {

			cPInstanceModels.add(newCPInstanceModel(cpDefinitionModel, i));
		}

		return cPInstanceModels;
	}

	public CPOptionCategoryModel newCPOptionCategoryModel(int index) {
		CPOptionCategoryModel cpOptionCategoryModel =
			new CPOptionCategoryModelImpl();

		// PK fields

		long cpOptionCategoryId = _counter.get();

		cpOptionCategoryModel.setCPOptionCategoryId(cpOptionCategoryId);

		// Audit fields

		cpOptionCategoryModel.setCompanyId(_companyId);
		cpOptionCategoryModel.setUserId(_sampleUserId);
		cpOptionCategoryModel.setUserName(_SAMPLE_USER_NAME);
		cpOptionCategoryModel.setCreateDate(new Date());
		cpOptionCategoryModel.setModifiedDate(new Date());

		// Other fields

		cpOptionCategoryModel.setTitle("Option Category" + index);
		cpOptionCategoryModel.setDescription(
			"Description for option category with ID " + cpOptionCategoryId);
		cpOptionCategoryModel.setPriority(index - 1);
		cpOptionCategoryModel.setKey("key" + index);
		cpOptionCategoryModel.setLastPublishDate(null);

		// Autogenerated fields

		cpOptionCategoryModel.setUuid(SequentialUUID.generate());

		return cpOptionCategoryModel;
	}

	public List<CPOptionCategoryModel> newCPOptionCategoryModels() {
		List<CPOptionCategoryModel> cpOptionCategoryModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_OPTION_CATEGORY_COUNT);

		for (int i = 1;
			 i <=
				 BenchmarksPropsValues.
					 MAX_COMMERCE_PRODUCT_OPTION_CATEGORY_COUNT;
			 i++) {

			cpOptionCategoryModels.add(newCPOptionCategoryModel(i));
		}

		return cpOptionCategoryModels;
	}

	public CPOptionModel newCPOptionModel(
		String commerceOptionTypeKey, int index) {

		CPOptionModel cpOptionModel = new CPOptionModelImpl();

		// PK fields

		cpOptionModel.setCPOptionId(_counter.get());

		// Audit fields

		cpOptionModel.setCompanyId(_companyId);
		cpOptionModel.setUserId(_sampleUserId);
		cpOptionModel.setUserName(_SAMPLE_USER_NAME);
		cpOptionModel.setCreateDate(new Date());
		cpOptionModel.setModifiedDate(new Date());

		// Other fields

		cpOptionModel.setName(
			StringBundler.concat(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root ",
				"available-locales=\"en_US\" default-locale=\"en_US\">",
				"<Name language-id=\"en_US\">Option Name ", index,
				"</Name></root>"));
		cpOptionModel.setDescription("Option Description");
		cpOptionModel.setCommerceOptionTypeKey(commerceOptionTypeKey);
		cpOptionModel.setFacetable(true);
		cpOptionModel.setRequired(true);
		cpOptionModel.setSkuContributor(true);
		cpOptionModel.setKey("option-name-" + index);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		cpOptionModel.setUuid(uuid);
		cpOptionModel.setExternalReferenceCode(uuid);

		return cpOptionModel;
	}

	public CPOptionValueModel newCPOptionValueModel(
		long cpOptionId, int index) {

		CPOptionValueModel cpOptionValueModel = new CPOptionValueModelImpl();

		// PK fields

		cpOptionValueModel.setCPOptionValueId(_counter.get());

		// Audit fields

		cpOptionValueModel.setCompanyId(_companyId);
		cpOptionValueModel.setUserId(_sampleUserId);
		cpOptionValueModel.setUserName(_SAMPLE_USER_NAME);
		cpOptionValueModel.setCreateDate(new Date());
		cpOptionValueModel.setModifiedDate(new Date());

		// Other fields

		cpOptionValueModel.setCPOptionId(cpOptionId);
		cpOptionValueModel.setName(
			StringBundler.concat(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root available-",
				"locales=\"en_US\" default-locale=\"en_US\"><Name language-id",
				"=\"en_US\">Option Value Name ", index, "</Name></root>"));
		cpOptionValueModel.setPriority(index - 1);
		cpOptionValueModel.setKey("option-value-" + index);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		cpOptionValueModel.setUuid(uuid);
		cpOptionValueModel.setExternalReferenceCode(uuid);

		return cpOptionValueModel;
	}

	public CProductModel newCProductModel(long groupId) {
		CProductModel cProductModel = new CProductModelImpl();

		// PK fields

		cProductModel.setCProductId(_counter.get());

		// Group instance

		cProductModel.setGroupId(groupId);

		// Audit fields

		cProductModel.setCompanyId(_companyId);
		cProductModel.setUserId(_sampleUserId);
		cProductModel.setUserName(_SAMPLE_USER_NAME);
		cProductModel.setCreateDate(new Date());
		cProductModel.setModifiedDate(new Date());

		// Other fields

		cProductModel.setPublishedCPDefinitionId(_counter.get());
		cProductModel.setLatestVersion(
			BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_DEFINITION_COUNT);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		cProductModel.setUuid(uuid);
		cProductModel.setExternalReferenceCode(uuid);

		return cProductModel;
	}

	public List<CProductModel> newCProductModels(long groupId) {
		List<CProductModel> cProductModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_COUNT);

		int count = (int)Math.ceil(
			BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_COUNT /
				BenchmarksPropsValues.MAX_COMMERCE_CATALOG_COUNT);

		if (BenchmarksPropsValues.MAX_COMMERCE_CATALOG_COUNT >
				BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_COUNT) {

			count = BenchmarksPropsValues.MAX_COMMERCE_CATALOG_COUNT;
		}

		for (int i = 1; i <= count; i++) {
			cProductModels.add(newCProductModel(groupId));
		}

		return cProductModels;
	}

	public CPSpecificationOptionModel newCPSpecificationOptionModel(
		long cpOptionCategoryId, int index) {

		CPSpecificationOptionModel cpSpecificationOptionModel =
			new CPSpecificationOptionModelImpl();

		// PK fields

		long cpSpecificationOptionId = _counter.get();

		cpSpecificationOptionModel.setCPSpecificationOptionId(
			cpSpecificationOptionId);

		// Audit fields

		cpSpecificationOptionModel.setCompanyId(_companyId);
		cpSpecificationOptionModel.setUserId(_sampleUserId);
		cpSpecificationOptionModel.setUserName(_SAMPLE_USER_NAME);
		cpSpecificationOptionModel.setCreateDate(new Date());
		cpSpecificationOptionModel.setModifiedDate(new Date());

		// Other fields

		cpSpecificationOptionModel.setCPOptionCategoryId(cpOptionCategoryId);
		cpSpecificationOptionModel.setTitle("Specification Option " + index);
		cpSpecificationOptionModel.setDescription(
			"Description for specification option with ID " +
				cpSpecificationOptionId);
		cpSpecificationOptionModel.setFacetable(false);
		cpSpecificationOptionModel.setKey("specification-option-" + index);
		cpSpecificationOptionModel.setLastPublishDate(null);

		// Autogenerated fields

		cpSpecificationOptionModel.setUuid(SequentialUUID.generate());

		return cpSpecificationOptionModel;
	}

	public List<CPSpecificationOptionModel> newCPSpecificationOptionModels(
		List<CPOptionCategoryModel> cpOptionCategoryModels) {

		List<CPSpecificationOptionModel> cpSpecificationOptionModels =
			new ArrayList<>(
				BenchmarksPropsValues.MAX_CP_SPECIFICATION_OPTION_COUNT);

		CPOptionCategoryModel cpOptionCategoryModel = null;

		for (int i = 1;
			 i <= BenchmarksPropsValues.MAX_CP_SPECIFICATION_OPTION_COUNT;
			 i++) {

			if (cpOptionCategoryModels.size() >= i) {
				cpOptionCategoryModel = cpOptionCategoryModels.get(i - 1);
			}

			cpSpecificationOptionModels.add(
				newCPSpecificationOptionModel(
					cpOptionCategoryModel.getCPOptionCategoryId(), i));
		}

		return cpSpecificationOptionModels;
	}

	public CPTaxCategoryModel newCPTaxCategoryModel() {
		CPTaxCategoryModel cpTaxCategoryModel = new CPTaxCategoryModelImpl();

		// PK fields

		cpTaxCategoryModel.setCPTaxCategoryId(_counter.get());

		// Audit fields

		cpTaxCategoryModel.setCompanyId(_companyId);
		cpTaxCategoryModel.setUserId(_sampleUserId);
		cpTaxCategoryModel.setUserName(_SAMPLE_USER_NAME);
		cpTaxCategoryModel.setCreateDate(new Date());
		cpTaxCategoryModel.setModifiedDate(new Date());

		// Other fields

		cpTaxCategoryModel.setName(
			StringBundler.concat(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root ",
				"available-locales=\"en_US\" default-locale=\"en_US\"><Name ",
				"language-id=\"en_US\">Normal Product</Name></root>"));
		cpTaxCategoryModel.setDescription(null);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		cpTaxCategoryModel.setUuid(uuid);
		cpTaxCategoryModel.setExternalReferenceCode(uuid);

		return cpTaxCategoryModel;
	}

	public DDMStructureLayoutModel newDDLDDMStructureLayoutModel(
		long groupId, DDMStructureVersionModel ddmStructureVersionModel) {

		StringBundler sb = new StringBundler(
			3 + (BenchmarksPropsValues.MAX_DDL_CUSTOM_FIELD_COUNT * 4));

		sb.append("{\"defaultLanguageId\": \"en_US\", \"pages\": [{\"rows\": ");
		sb.append("[");

		for (int i = 0; i < BenchmarksPropsValues.MAX_DDL_CUSTOM_FIELD_COUNT;
			 i++) {

			sb.append("{\"columns\": [{\"fieldNames\": [\"");
			sb.append(nextDDLCustomFieldName(groupId, i));
			sb.append("\"], \"size\": 12}]}");
			sb.append(", ");
		}

		if (BenchmarksPropsValues.MAX_DDL_CUSTOM_FIELD_COUNT > 0) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append("], \"title\": {\"en_US\": \"\"}}],\"paginationMode\": ");
		sb.append("\"single-page\"}");

		return newDDMStructureLayoutModel(
			_globalGroupId, _guestUserId,
			ddmStructureVersionModel.getStructureVersionId(), sb.toString());
	}

	public DDMStructureModel newDDLDDMStructureModel(long groupId) {
		StringBundler sb = new StringBundler(
			3 + (BenchmarksPropsValues.MAX_DDL_CUSTOM_FIELD_COUNT * 9));

		sb.append("{\"availableLanguageIds\": [\"en_US\"],");
		sb.append("\"defaultLanguageId\": \"en_US\", \"fields\": [");

		for (int i = 0; i < BenchmarksPropsValues.MAX_DDL_CUSTOM_FIELD_COUNT;
			 i++) {

			sb.append("{\"dataType\": \"string\", \"indexType\": ");
			sb.append("\"keyword\", \"label\": {\"en_US\": \"Text");
			sb.append(i);
			sb.append("\"}, \"name\": \"");
			sb.append(nextDDLCustomFieldName(groupId, i));
			sb.append("\", \"readOnly\": false, \"repeatable\": false,");
			sb.append("\"required\": false, \"showLabel\": true, \"type\": ");
			sb.append("\"text\"}");
			sb.append(",");
		}

		if (BenchmarksPropsValues.MAX_DDL_CUSTOM_FIELD_COUNT > 0) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append("]}");

		return newDDMStructureModel(
			groupId, _sampleUserId, getClassNameId(DDLRecordSet.class),
			"Test DDM Structure", sb.toString(), _counter.get());
	}

	public List<PortletPreferencesModel> newDDLPortletPreferencesModels(
		long plid) {

		return ListUtil.fromArray(
			newPortletPreferencesModel(
				plid, DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY),
			newPortletPreferencesModel(plid, DDLPortletKeys.DYNAMIC_DATA_LISTS),
			newPortletPreferencesModel(
				plid, DDMPortletKeys.DYNAMIC_DATA_MAPPING));
	}

	public List<PortletPreferenceValueModel> newDDLPortletPreferenceValueModels(
		PortletPreferencesModel portletPreferencesModel,
		DDLRecordSetModel ddlRecordSetModel) {

		return Arrays.asList(
			newPortletPreferenceValueModel(
				portletPreferencesModel, "editable", 0, "true"),
			newPortletPreferenceValueModel(
				portletPreferencesModel, "recordSetId", 0,
				String.valueOf(ddlRecordSetModel.getRecordSetId())),
			newPortletPreferenceValueModel(
				portletPreferencesModel, "spreadsheet", 0, "false"));
	}

	public DDLRecordModel newDDLRecordModel(
		DDLRecordSetModel dDLRecordSetModel) {

		DDLRecordModel ddlRecordModel = new DDLRecordModelImpl();

		// PK fields

		ddlRecordModel.setRecordId(_counter.get());

		// Group instance

		ddlRecordModel.setGroupId(dDLRecordSetModel.getGroupId());

		// Audit fields

		ddlRecordModel.setCompanyId(_companyId);
		ddlRecordModel.setUserId(_sampleUserId);
		ddlRecordModel.setUserName(_SAMPLE_USER_NAME);
		ddlRecordModel.setVersionUserId(_sampleUserId);
		ddlRecordModel.setVersionUserName(_SAMPLE_USER_NAME);
		ddlRecordModel.setCreateDate(new Date());
		ddlRecordModel.setModifiedDate(new Date());

		// Other fields

		ddlRecordModel.setDDMStorageId(_counter.get());
		ddlRecordModel.setRecordSetId(dDLRecordSetModel.getRecordSetId());
		ddlRecordModel.setVersion(DDLRecordConstants.VERSION_DEFAULT);
		ddlRecordModel.setDisplayIndex(
			DDLRecordConstants.DISPLAY_INDEX_DEFAULT);
		ddlRecordModel.setLastPublishDate(new Date());

		// Autogenerated fields

		ddlRecordModel.setUuid(SequentialUUID.generate());

		return ddlRecordModel;
	}

	public DDLRecordSetModel newDDLRecordSetModel(
		DDMStructureModel ddmStructureModel, int currentIndex) {

		DDLRecordSetModel ddlRecordSetModel = new DDLRecordSetModelImpl();

		// PK fields

		ddlRecordSetModel.setRecordSetId(_counter.get());

		// Group instance

		ddlRecordSetModel.setGroupId(ddmStructureModel.getGroupId());

		// Audit fields

		ddlRecordSetModel.setCompanyId(_companyId);
		ddlRecordSetModel.setUserId(_sampleUserId);
		ddlRecordSetModel.setUserName(_SAMPLE_USER_NAME);
		ddlRecordSetModel.setCreateDate(new Date());
		ddlRecordSetModel.setModifiedDate(new Date());

		// Other fields

		ddlRecordSetModel.setDDMStructureId(ddmStructureModel.getStructureId());
		ddlRecordSetModel.setRecordSetKey(String.valueOf(_counter.get()));
		ddlRecordSetModel.setName(
			StringBundler.concat(
				"<?xml version=\"1.0\"?><root available-locales=\"en_US\" ",
				"default-locale=\"en_US\"><Name language-id=\"en_US\">",
				"Test DDL Record Set ", currentIndex, "</Name></root>"));
		ddlRecordSetModel.setMinDisplayRows(
			DDLRecordSetConstants.MIN_DISPLAY_ROWS_DEFAULT);
		ddlRecordSetModel.setScope(
			DDLRecordSetConstants.SCOPE_DYNAMIC_DATA_LISTS);
		ddlRecordSetModel.setSettings(StringPool.BLANK);
		ddlRecordSetModel.setLastPublishDate(new Date());

		// Autogenerated fields

		ddlRecordSetModel.setUuid(SequentialUUID.generate());

		return ddlRecordSetModel;
	}

	public DDLRecordVersionModel newDDLRecordVersionModel(
		DDLRecordModel dDLRecordModel) {

		DDLRecordVersionModel ddlRecordVersionModel =
			new DDLRecordVersionModelImpl();

		// PK fields

		ddlRecordVersionModel.setRecordVersionId(_counter.get());

		// Group instance

		ddlRecordVersionModel.setGroupId(dDLRecordModel.getGroupId());

		// Audit fields

		ddlRecordVersionModel.setCompanyId(_companyId);
		ddlRecordVersionModel.setUserId(_sampleUserId);
		ddlRecordVersionModel.setUserName(_SAMPLE_USER_NAME);
		ddlRecordVersionModel.setCreateDate(dDLRecordModel.getModifiedDate());

		// Other fields

		ddlRecordVersionModel.setDDMStorageId(dDLRecordModel.getDDMStorageId());
		ddlRecordVersionModel.setRecordSetId(dDLRecordModel.getRecordSetId());
		ddlRecordVersionModel.setRecordId(dDLRecordModel.getRecordId());
		ddlRecordVersionModel.setVersion(dDLRecordModel.getVersion());
		ddlRecordVersionModel.setDisplayIndex(dDLRecordModel.getDisplayIndex());
		ddlRecordVersionModel.setStatus(WorkflowConstants.STATUS_APPROVED);
		ddlRecordVersionModel.setStatusDate(dDLRecordModel.getModifiedDate());

		return ddlRecordVersionModel;
	}

	public List<DDMFieldAttributeModel> newDDMFieldAttributeModels(
		DLFileEntryModel dlFileEntryModel, List<DDMFieldModel> ddmFieldModels,
		DDMStorageLinkModel ddmStorageLinkModel) {

		return Arrays.asList(
			newDDMFieldAttributeModel(
				ddmFieldModels.get(0), ddmStorageLinkModel.getClassPK(),
				"availableLanguageIds", StringPool.BLANK, "en_US"),
			newDDMFieldAttributeModel(
				ddmFieldModels.get(0), ddmStorageLinkModel.getClassPK(),
				"defaultLanguageId", StringPool.BLANK, "en_US"),
			newDDMFieldAttributeModel(
				ddmFieldModels.get(1), ddmStorageLinkModel.getClassPK(),
				StringPool.BLANK, "en_US", "text/plain"));
	}

	public List<DDMFieldAttributeModel> newDDMFieldAttributeModels(
		int currentIndex, DDLRecordModel ddlRecordModel,
		List<DDMFieldModel> ddmFieldModels,
		DDMStorageLinkModel ddmStorageLinkModel) {

		List<DDMFieldAttributeModel> ddmFieldAttributeModels =
			new ArrayList<>();

		ddmFieldAttributeModels.add(
			newDDMFieldAttributeModel(
				ddmFieldModels.get(0), ddmStorageLinkModel.getClassPK(),
				"availableLanguageIds", StringPool.BLANK, "en_US"));
		ddmFieldAttributeModels.add(
			newDDMFieldAttributeModel(
				ddmFieldModels.get(0), ddmStorageLinkModel.getClassPK(),
				"defaultLanguageId", StringPool.BLANK, "en_US"));

		for (int i = 1; i < ddmFieldModels.size(); i++) {
			DDMFieldModel ddmFieldModel = ddmFieldModels.get(i);

			ddmFieldAttributeModels.add(
				newDDMFieldAttributeModel(
					ddmFieldModel, ddmStorageLinkModel.getClassPK(),
					StringPool.BLANK, "en_US", "Test Record " + currentIndex));
		}

		return ddmFieldAttributeModels;
	}

	public List<DDMFieldAttributeModel> newDDMFieldAttributeModels(
		JournalArticleModel journalArticleModel,
		List<DDMFieldModel> ddmFieldModels) {

		List<DDMFieldAttributeModel> ddmFieldAttributeModels =
			new ArrayList<>();

		ddmFieldAttributeModels.add(
			newDDMFieldAttributeModel(
				ddmFieldModels.get(0), journalArticleModel.getId(),
				"availableLanguageIds", StringPool.BLANK, "en_US"));
		ddmFieldAttributeModels.add(
			newDDMFieldAttributeModel(
				ddmFieldModels.get(0), journalArticleModel.getId(),
				"defaultLanguageId", StringPool.BLANK, "en_US"));

		if (_journalArticleContent.length() >
				DDMFieldAttributeImpl.SMALL_ATTRIBUTE_VALUE_MAX_LENGTH) {

			ddmFieldAttributeModels.add(
				newDDMFieldAttributeModel(
					ddmFieldModels.get(1), journalArticleModel.getId(),
					StringPool.BLANK, "en_US", "", _journalArticleContent));
		}
		else {
			ddmFieldAttributeModels.add(
				newDDMFieldAttributeModel(
					ddmFieldModels.get(1), journalArticleModel.getId(),
					StringPool.BLANK, "en_US", _journalArticleContent));
		}

		return ddmFieldAttributeModels;
	}

	public List<DDMFieldModel> newDDMFieldModels(
		DLFileEntryModel dlFileEntryModel,
		DDMStorageLinkModel ddmStorageLinkModel) {

		return Arrays.asList(
			newDDMFieldModel(
				ddmStorageLinkModel.getClassPK(),
				_defaultDLDDMStructureVersionId, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK, false, 0),
			newDDMFieldModel(
				ddmStorageLinkModel.getClassPK(),
				_defaultDLDDMStructureVersionId, "CONTENT_TYPE", "string",
				StringUtil.randomId(), true, 1));
	}

	public List<DDMFieldModel> newDDMFieldModels(
		int currentIndex, DDLRecordModel ddlRecordModel,
		DDMStorageLinkModel ddmStorageLinkModel) {

		List<DDMFieldModel> ddmFieldModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_DDL_CUSTOM_FIELD_COUNT + 1);

		ddmFieldModels.add(
			newDDMFieldModel(
				ddmStorageLinkModel.getClassPK(),
				ddmStorageLinkModel.getStructureVersionId(), StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK, false, 0));

		for (int i = 1; i <= BenchmarksPropsValues.MAX_DDL_CUSTOM_FIELD_COUNT;
			 i++) {

			ddmFieldModels.add(
				newDDMFieldModel(
					ddmStorageLinkModel.getClassPK(),
					ddmStorageLinkModel.getStructureVersionId(),
					nextDDLCustomFieldName(ddlRecordModel.getGroupId(), i - 1),
					"string", StringUtil.randomString(), true, i));
		}

		return ddmFieldModels;
	}

	public List<DDMFieldModel> newDDMFieldModels(
		JournalArticleModel journalArticleModel) {

		return Arrays.asList(
			newDDMFieldModel(
				journalArticleModel.getId(),
				_defaultJournalDDMStructureVersionId, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK, false, 0),
			newDDMFieldModel(
				journalArticleModel.getId(),
				_defaultJournalDDMStructureVersionId, "content", "rich_text",
				StringUtil.randomId(), true, 1));
	}

	public DDMStorageLinkModel newDDMStorageLinkModel(
		DDLRecordModel ddlRecordModel, long ddmStorageLinkId, long structureId,
		long structureVersionId) {

		return newDDMStorageLinkModel(
			ddmStorageLinkId, ddlRecordModel.getDDMStorageId(), structureId,
			structureVersionId);
	}

	public DDMStorageLinkModel newDDMStorageLinkModel(
		DLFileEntryModel dlFileEntryModel, long ddmStorageLinkId,
		long structureId) {

		return newDDMStorageLinkModel(
			ddmStorageLinkId, _counter.get(), structureId,
			_defaultDLDDMStructureVersionId);
	}

	public DDMStorageLinkModel newDDMStorageLinkModel(
		JournalArticleModel journalArticleModel, long structureId) {

		DDMStorageLinkModel ddmStorageLinkModel = new DDMStorageLinkModelImpl();

		// PK fields

		ddmStorageLinkModel.setStorageLinkId(_counter.get());

		// Other fields

		ddmStorageLinkModel.setClassNameId(
			getClassNameId(JournalArticle.class));
		ddmStorageLinkModel.setClassPK(journalArticleModel.getId());
		ddmStorageLinkModel.setStructureId(structureId);
		ddmStorageLinkModel.setStructureVersionId(
			_defaultJournalDDMStructureVersionId);

		// Autogenerated fields

		ddmStorageLinkModel.setUuid(SequentialUUID.generate());

		return ddmStorageLinkModel;
	}

	public DDMStructureLinkModel newDDMStructureLinkModel(
		DDLRecordSetModel ddlRecordSetModel) {

		return newDDMStructureLinkModel(
			getClassNameId(DDLRecordSet.class),
			ddlRecordSetModel.getRecordSetId(),
			ddlRecordSetModel.getDDMStructureId());
	}

	public DDMStructureLinkModel newDDMStructureLinkModel(
		DLFileEntryMetadataModel dlFileEntryMetadataModel) {

		return newDDMStructureLinkModel(
			getClassNameId(DLFileEntryMetadata.class),
			dlFileEntryMetadataModel.getFileEntryMetadataId(),
			dlFileEntryMetadataModel.getDDMStructureId());
	}

	public DDMStructureVersionModel newDDMStructureVersionModel(
		DDMStructureModel ddmStructureModel) {

		return newDDMStructureVersionModel(ddmStructureModel, _counter.get());
	}

	public DDMStructureVersionModel newDDMStructureVersionModel(
		DDMStructureModel ddmStructureModel, long structureVersionId) {

		DDMStructureVersionModel ddmStructureVersionModel =
			new DDMStructureVersionModelImpl();

		// PK fields

		ddmStructureVersionModel.setStructureVersionId(structureVersionId);

		// Group instance

		ddmStructureVersionModel.setGroupId(ddmStructureModel.getGroupId());

		// Audit fields

		ddmStructureVersionModel.setCompanyId(_companyId);
		ddmStructureVersionModel.setUserId(ddmStructureModel.getUserId());
		ddmStructureVersionModel.setUserName(_SAMPLE_USER_NAME);
		ddmStructureVersionModel.setCreateDate(nextFutureDate());

		// Other fields

		ddmStructureVersionModel.setStructureId(
			ddmStructureModel.getStructureId());
		ddmStructureVersionModel.setVersion(
			DDMStructureConstants.VERSION_DEFAULT);
		ddmStructureVersionModel.setName(
			StringBundler.concat(
				"<?xml version=\"1.0\"?><root available-locales=\"en_US\" ",
				"default-locale=\"en_US\"><name language-id=\"en_US\">",
				ddmStructureModel.getStructureKey(), "</name></root>"));
		ddmStructureVersionModel.setDefinition(
			ddmStructureModel.getDefinition());
		ddmStructureVersionModel.setStorageType(StorageType.DEFAULT.toString());
		ddmStructureVersionModel.setStatusByUserId(
			ddmStructureModel.getUserId());
		ddmStructureVersionModel.setStatusByUserName(_SAMPLE_USER_NAME);
		ddmStructureVersionModel.setStatusDate(nextFutureDate());

		return ddmStructureVersionModel;
	}

	public DDMTemplateLinkModel newDDMTemplateLinkModel(
		JournalArticleModel journalArticleModel, long templateId) {

		DDMTemplateLinkModel ddmTemplateLinkModel =
			new DDMTemplateLinkModelImpl();

		// PK fields

		ddmTemplateLinkModel.setTemplateLinkId(_counter.get());

		// Audit fields

		ddmTemplateLinkModel.setCompanyId(_companyId);

		// Other fields

		ddmTemplateLinkModel.setClassNameId(
			getClassNameId(JournalArticle.class));
		ddmTemplateLinkModel.setClassPK(journalArticleModel.getId());
		ddmTemplateLinkModel.setTemplateId(templateId);

		return ddmTemplateLinkModel;
	}

	public UserModel newDefaultAdminUserModel() {
		return newUserModel(
			_counter.get(), "Test", "Test", "Test", UserConstants.TYPE_REGULAR);
	}

	public AssetVocabularyModel newDefaultAssetVocabularyModel() {
		return newAssetVocabularyModel(
			_globalGroupId, _guestUserId, null,
			PropsValues.ASSET_VOCABULARY_DEFAULT);
	}

	public CompanyModel newDefaultCompanyModel() {
		return _newCompanyModel("liferay.com");
	}

	public DDMStructureLayoutModel newDefaultDLDDMStructureLayoutModel() {
		return newDDMStructureLayoutModel(
			_globalGroupId, _guestUserId, _defaultDLDDMStructureVersionId,
			_dlDDMStructureLayoutContent);
	}

	public DDMStructureModel newDefaultDLDDMStructureModel() {
		_defaultDLDDMStructureId = _counter.get();

		return newDDMStructureModel(
			_globalGroupId, _guestUserId, getClassNameId(DLFileEntry.class),
			RawMetadataProcessor.TIKA_RAW_METADATA, _dlDDMStructureContent,
			_defaultDLDDMStructureId);
	}

	public DDMStructureVersionModel newDefaultDLDDMStructureVersionModel(
		DDMStructureModel ddmStructureModel) {

		_defaultDLDDMStructureVersionId = _counter.get();

		return newDDMStructureVersionModel(
			ddmStructureModel, _defaultDLDDMStructureVersionId);
	}

	public DDMStructureLayoutModel newDefaultJournalDDMStructureLayoutModel() {
		return newDDMStructureLayoutModel(
			_globalGroupId, _guestUserId, _defaultJournalDDMStructureVersionId,
			_journalDDMStructureLayoutContent,
			getClassNameId(JournalArticle.class), _JOURNAL_STRUCTURE_KEY);
	}

	public DDMStructureModel newDefaultJournalDDMStructureModel() {
		_defaultJournalDDMStructureId = _counter.get();

		return newDDMStructureModel(
			_globalGroupId, _guestUserId, getClassNameId(JournalArticle.class),
			_JOURNAL_STRUCTURE_KEY, _journalDDMStructureContent,
			_defaultJournalDDMStructureId);
	}

	public DDMStructureVersionModel newDefaultJournalDDMStructureVersionModel(
		DDMStructureModel ddmStructureModel) {

		_defaultJournalDDMStructureVersionId = _counter.get();

		return newDDMStructureVersionModel(
			ddmStructureModel, _defaultJournalDDMStructureVersionId);
	}

	public DDMTemplateModel newDefaultJournalDDMTemplateModel() {
		_defaultJournalDDMTemplateId = _counter.get();

		return newDDMTemplateModel(
			_globalGroupId, _guestUserId, _defaultJournalDDMStructureId,
			getClassNameId(JournalArticle.class), _defaultJournalDDMTemplateId);
	}

	public DDMTemplateVersionModel newDefaultJournalDDMTemplateVersionModel() {
		DDMTemplateVersionModelImpl ddmTemplateVersionModelImpl =
			new DDMTemplateVersionModelImpl();

		// PK fields

		ddmTemplateVersionModelImpl.setTemplateVersionId(_counter.get());

		// Group instance

		ddmTemplateVersionModelImpl.setGroupId(_globalGroupId);

		// Audit fields

		ddmTemplateVersionModelImpl.setCompanyId(_companyId);
		ddmTemplateVersionModelImpl.setUserId(_guestUserId);
		ddmTemplateVersionModelImpl.setCreateDate(nextFutureDate());

		// Other fields

		ddmTemplateVersionModelImpl.setClassNameId(
			getClassNameId(DDMStructure.class));
		ddmTemplateVersionModelImpl.setClassPK(_defaultJournalDDMStructureId);
		ddmTemplateVersionModelImpl.setTemplateId(_defaultJournalDDMTemplateId);
		ddmTemplateVersionModelImpl.setVersion(
			DDMTemplateConstants.VERSION_DEFAULT);
		ddmTemplateVersionModelImpl.setName(
			StringBundler.concat(
				"<?xml version=\"1.0\"?><root available-locales=\"en_US\" ",
				"default-locale=\"en_US\"><name language-id=\"en_US\">",
				_JOURNAL_STRUCTURE_KEY, "</name></root>"));
		ddmTemplateVersionModelImpl.setStatusByUserId(_guestUserId);
		ddmTemplateVersionModelImpl.setStatusDate(nextFutureDate());

		return ddmTemplateVersionModelImpl;
	}

	public UserModel newDefaultServiceAccountUserModel() {
		return newUserModel(
			_counter.get(), "default-service-account",
			"default-service-account", "default-service-account",
			UserConstants.TYPE_DEFAULT_SERVICE_ACCOUNT);
	}

	public DLFileEntryMetadataModel newDLFileEntryMetadataModel(
		long ddmStorageLinkId, long ddmStructureId,
		DLFileVersionModel dlFileVersionModel) {

		DLFileEntryMetadataModel dlFileEntryMetadataModel =
			new DLFileEntryMetadataModelImpl();

		// PK fields

		dlFileEntryMetadataModel.setFileEntryMetadataId(_counter.get());

		// Other fields

		dlFileEntryMetadataModel.setDDMStorageId(ddmStorageLinkId);
		dlFileEntryMetadataModel.setDDMStructureId(ddmStructureId);
		dlFileEntryMetadataModel.setFileEntryId(
			dlFileVersionModel.getFileEntryId());
		dlFileEntryMetadataModel.setFileVersionId(
			dlFileVersionModel.getFileVersionId());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		dlFileEntryMetadataModel.setUuid(uuid);
		dlFileEntryMetadataModel.setExternalReferenceCode(uuid);

		return dlFileEntryMetadataModel;
	}

	public DLFileEntryModel newDLFileEntryModel(
		DLFolderModel dlFolderModel, String name, String extension,
		String mimeType, long fileEntryId) {

		DLFileEntryModel dlFileEntryModel = new DLFileEntryModelImpl();

		// PK fields

		dlFileEntryModel.setFileEntryId(fileEntryId);

		// Group instance

		dlFileEntryModel.setGroupId(dlFolderModel.getGroupId());

		// Audit fields

		dlFileEntryModel.setCompanyId(_companyId);
		dlFileEntryModel.setUserId(_sampleUserId);
		dlFileEntryModel.setUserName(_SAMPLE_USER_NAME);
		dlFileEntryModel.setCreateDate(nextFutureDate());
		dlFileEntryModel.setModifiedDate(nextFutureDate());

		// Other fields

		dlFileEntryModel.setRepositoryId(dlFolderModel.getRepositoryId());
		dlFileEntryModel.setFolderId(dlFolderModel.getFolderId());
		dlFileEntryModel.setTreePath(dlFolderModel.getTreePath());
		dlFileEntryModel.setName(String.valueOf(_dlFileEntryIdCounter.get()));
		dlFileEntryModel.setFileName(name + "." + extension);
		dlFileEntryModel.setExtension(extension);
		dlFileEntryModel.setMimeType(mimeType);
		dlFileEntryModel.setTitle(name);
		dlFileEntryModel.setFileEntryTypeId(
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);
		dlFileEntryModel.setVersion(DLFileEntryConstants.VERSION_DEFAULT);
		dlFileEntryModel.setSize(BenchmarksPropsValues.MAX_DL_FILE_ENTRY_SIZE);
		dlFileEntryModel.setLastPublishDate(nextFutureDate());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		dlFileEntryModel.setUuid(uuid);
		dlFileEntryModel.setExternalReferenceCode(uuid);

		return dlFileEntryModel;
	}

	public List<DLFileEntryModel> newDLFileEntryModels(
		DLFolderModel dlFolderModel) {

		List<DLFileEntryModel> dlFileEntryModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_DL_FILE_ENTRY_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_DL_FILE_ENTRY_COUNT;
			 i++) {

			dlFileEntryModels.add(
				newDLFileEntryModel(
					dlFolderModel, "TestFile" + i, "txt",
					ContentTypes.TEXT_PLAIN, _counter.get()));
		}

		return dlFileEntryModels;
	}

	public DLFileEntryTypeModel newDLFileEntryTypeModel() {
		DLFileEntryTypeModel defaultDLFileEntryTypeModel =
			new DLFileEntryTypeModelImpl();

		// PK fields

		defaultDLFileEntryTypeModel.setFileEntryTypeId(
			_DEFAULT_DL_FILE_ENTRY_TYPE_ID);

		// Audit fields

		defaultDLFileEntryTypeModel.setCreateDate(nextFutureDate());
		defaultDLFileEntryTypeModel.setModifiedDate(nextFutureDate());

		// Other fields

		defaultDLFileEntryTypeModel.setFileEntryTypeKey(
			StringUtil.toUpperCase(
				DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT));
		defaultDLFileEntryTypeModel.setName(
			StringBundler.concat(
				"<?xml version=\"1.0\"?><root available-locales=\"en_US\" ",
				"default-locale=\"en_US\"><name language-id=\"en_US\">",
				DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT,
				"</name></root>"));
		defaultDLFileEntryTypeModel.setLastPublishDate(nextFutureDate());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		defaultDLFileEntryTypeModel.setUuid(uuid);
		defaultDLFileEntryTypeModel.setExternalReferenceCode(uuid);

		return defaultDLFileEntryTypeModel;
	}

	public DLFileVersionModel newDLFileVersionModel(
		DLFileEntryModel dlFileEntryModel) {

		DLFileVersionModel dlFileVersionModel = new DLFileVersionModelImpl();

		// PK fields

		dlFileVersionModel.setFileVersionId(_counter.get());

		// Group instance

		dlFileVersionModel.setGroupId(dlFileEntryModel.getGroupId());

		// Audit fields

		dlFileVersionModel.setCompanyId(_companyId);
		dlFileVersionModel.setUserId(_sampleUserId);
		dlFileVersionModel.setUserName(_SAMPLE_USER_NAME);
		dlFileVersionModel.setCreateDate(nextFutureDate());
		dlFileVersionModel.setModifiedDate(nextFutureDate());

		// Other fields

		dlFileVersionModel.setRepositoryId(dlFileEntryModel.getRepositoryId());
		dlFileVersionModel.setFolderId(dlFileEntryModel.getFolderId());
		dlFileVersionModel.setFileEntryId(dlFileEntryModel.getFileEntryId());
		dlFileVersionModel.setFileName(dlFileEntryModel.getFileName());
		dlFileVersionModel.setExtension(dlFileEntryModel.getExtension());
		dlFileVersionModel.setMimeType(dlFileEntryModel.getMimeType());
		dlFileVersionModel.setTitle(dlFileEntryModel.getTitle());
		dlFileVersionModel.setFileEntryTypeId(
			dlFileEntryModel.getFileEntryTypeId());
		dlFileVersionModel.setVersion(dlFileEntryModel.getVersion());
		dlFileVersionModel.setSize(dlFileEntryModel.getSize());
		dlFileVersionModel.setLastPublishDate(nextFutureDate());

		// Autogenerated fields

		dlFileVersionModel.setUuid(SequentialUUID.generate());

		return dlFileVersionModel;
	}

	public DLFolderModel newDLFolderModel(
		long groupId, long parentFolderId, String name) {

		return newDLFolderModel(
			_counter.get(), groupId, parentFolderId, "", name);
	}

	public DLFolderModel newDLFolderModel(String name) {
		return newDLFolderModel(_counter.get(), _globalGroupId, 0, "", name);
	}

	public List<DLFolderModel> newDLFolderModels(
		long groupId, long parentFolderId, int depth) {

		List<DLFolderModel> dlFolderModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_DL_FOLDER_COUNT);

		Map<Long, String> treePaths = _treePathsMap.computeIfAbsent(
			depth, HashMap::new);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_DL_FOLDER_COUNT; i++) {
			long folderId = _counter.get();

			StringBundler sb = new StringBundler(3);

			if (depth == 1) {
				sb.append(StringPool.FORWARD_SLASH);
				sb.append(folderId);
				sb.append(StringPool.FORWARD_SLASH);
			}
			else {
				Map<Long, String> parentTreePaths = _treePathsMap.get(
					depth - 1);

				sb.append(parentTreePaths.get(parentFolderId));

				sb.append(folderId);
				sb.append(StringPool.FORWARD_SLASH);
			}

			treePaths.put(folderId, sb.toString());

			dlFolderModels.add(
				newDLFolderModel(
					folderId, groupId, parentFolderId, sb.toString(),
					"Test Folder " + i));
		}

		return dlFolderModels;
	}

	public FragmentCollectionModel newFragmentCollectionModel(long groupId) {
		FragmentCollectionModel fragmentCollectionModel =
			new FragmentCollectionModelImpl();

		// PK fields

		fragmentCollectionModel.setFragmentCollectionId(_counter.get());

		// Group instance

		fragmentCollectionModel.setGroupId(groupId);

		// Audit fields

		fragmentCollectionModel.setCompanyId(_companyId);
		fragmentCollectionModel.setUserId(_sampleUserId);
		fragmentCollectionModel.setCreateDate(new Date());
		fragmentCollectionModel.setModifiedDate(new Date());

		// Other fields

		fragmentCollectionModel.setFragmentCollectionKey("fragmentcollection");
		fragmentCollectionModel.setName("fragmentcollection");

		// Autogenerated fields

		fragmentCollectionModel.setUuid(SequentialUUID.generate());

		return fragmentCollectionModel;
	}

	public FragmentEntryLinkModel newFragmentEntryLinkModel(
		LayoutModel layoutModel, FragmentEntryModel fragmentEntryModel) {

		FragmentEntryLinkModel fragmentEntryLinkModel =
			new FragmentEntryLinkModelImpl();

		// PK fields

		fragmentEntryLinkModel.setFragmentEntryLinkId(_counter.get());

		// Group instance

		fragmentEntryLinkModel.setGroupId(fragmentEntryModel.getGroupId());

		// Audit fields

		fragmentEntryLinkModel.setCompanyId(_companyId);
		fragmentEntryLinkModel.setUserId(_sampleUserId);
		fragmentEntryLinkModel.setUserName(_SAMPLE_USER_NAME);
		fragmentEntryLinkModel.setCreateDate(new Date());
		fragmentEntryLinkModel.setModifiedDate(new Date());

		// Other fields

		fragmentEntryLinkModel.setFragmentEntryId(
			fragmentEntryModel.getFragmentEntryId());
		fragmentEntryLinkModel.setClassNameId(getClassNameId(Layout.class));
		fragmentEntryLinkModel.setClassPK(layoutModel.getPlid());
		fragmentEntryLinkModel.setCss(fragmentEntryModel.getCss());
		fragmentEntryLinkModel.setHtml(fragmentEntryModel.getHtml());
		fragmentEntryLinkModel.setJs(fragmentEntryModel.getJs());
		fragmentEntryLinkModel.setEditableValues(StringPool.BLANK);
		fragmentEntryLinkModel.setNamespace(StringUtil.randomId());
		fragmentEntryLinkModel.setPosition(0);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		fragmentEntryLinkModel.setUuid(uuid);
		fragmentEntryLinkModel.setExternalReferenceCode(uuid);

		return fragmentEntryLinkModel;
	}

	public List<FragmentEntryLinkModel> newFragmentEntryLinkModels(
			JournalArticleModel journalArticleModel, LayoutModel layoutModel,
			long segmentsExperienceId)
		throws Exception {

		List<FragmentEntryLinkModel> fragmentEntryLinkModels =
			new ArrayList<>();

		for (int i = 0; i < BenchmarksPropsValues.MAX_FRAGMENT_ENTRY_LINK_COUNT;
			 i++) {

			fragmentEntryLinkModels.add(
				newFragmentEntryLinkModel(
					journalArticleModel, layoutModel, segmentsExperienceId));
		}

		return fragmentEntryLinkModels;
	}

	public List<FragmentEntryLinkModel> newFragmentEntryLinkModels(
			List<LayoutModel> layoutModels, long segmentsExperienceId)
		throws Exception {

		List<FragmentEntryLinkModel> nonhiddenFragmentEntryLinkModels =
			new ArrayList<>();

		LayoutModel nonhiddenLayoutModel = null;

		String imageRenderNamespace = StringUtil.randomId();
		String paragraphRenderNamespace = StringUtil.randomId();

		for (LayoutModel layoutModel : layoutModels) {
			if (!layoutModel.isHidden()) {
				nonhiddenLayoutModel = layoutModel;

				continue;
			}

			nonhiddenFragmentEntryLinkModels.add(
				newFragmentEntryLinkModel(
					layoutModel, 0, segmentsExperienceId,
					_readFile(
						_getFragmentComponentInputStream("paragraph", "css")),
					_readFile(
						_getFragmentComponentInputStream("paragraph", "html")),
					StringPool.BLANK,
					_readFile(
						"fragment_component" +
							"/fragment_component_paragraph_title_editValue." +
								"json"),
					paragraphRenderNamespace, 0,
					_FRAGMENT_COMPONENT_RENDER_KEY_PARAGRAPH));
			nonhiddenFragmentEntryLinkModels.add(
				newFragmentEntryLinkModel(
					layoutModel, 0, segmentsExperienceId,
					_readFile(
						_getFragmentComponentInputStream("paragraph", "css")),
					_readFile(
						_getFragmentComponentInputStream("paragraph", "html")),
					StringPool.BLANK,
					_readFile(
						"fragment_component" +
							"/fragment_component_paragraph_content_editValue." +
								"json"),
					paragraphRenderNamespace, 1,
					_FRAGMENT_COMPONENT_RENDER_KEY_PARAGRAPH));
			nonhiddenFragmentEntryLinkModels.add(
				newFragmentEntryLinkModel(
					layoutModel, 0, segmentsExperienceId, "",
					_readFile(
						_getFragmentComponentInputStream("image", "html")),
					_readFile(
						"fragment_component" +
							"/fragment_component_image_configuration.json"),
					_readFile(
						"fragment_component" +
							"/fragment_component_image_editValue.json"),
					imageRenderNamespace, 0,
					_FRAGMENT_COMPONENT_RENDER_KEY_IMAGE));
		}

		List<FragmentEntryLinkModel> fragmentEntryLinkModels = new ArrayList<>(
			nonhiddenFragmentEntryLinkModels);

		for (FragmentEntryLinkModel originalFragmentEntryLinkModel :
				nonhiddenFragmentEntryLinkModels) {

			fragmentEntryLinkModels.add(
				newFragmentEntryLinkModel(
					nonhiddenLayoutModel,
					originalFragmentEntryLinkModel.getFragmentEntryLinkId(),
					originalFragmentEntryLinkModel.getSegmentsExperienceId(),
					originalFragmentEntryLinkModel.getCss(),
					originalFragmentEntryLinkModel.getHtml(),
					originalFragmentEntryLinkModel.getConfiguration(),
					originalFragmentEntryLinkModel.getEditableValues(),
					originalFragmentEntryLinkModel.getNamespace(),
					originalFragmentEntryLinkModel.getPosition(),
					originalFragmentEntryLinkModel.getRendererKey()));
		}

		return fragmentEntryLinkModels;
	}

	public FragmentEntryModel newFragmentEntryModel(
			long groupId, FragmentCollectionModel fragmentCollectionModel)
		throws Exception {

		FragmentEntryModel fragmentEntryModel = new FragmentEntryModelImpl();

		// PK fields

		fragmentEntryModel.setFragmentEntryId(_counter.get());

		// Group instance

		fragmentEntryModel.setGroupId(groupId);

		// Audit fields

		fragmentEntryModel.setCompanyId(_companyId);
		fragmentEntryModel.setUserId(_sampleUserId);
		fragmentEntryModel.setUserName(_SAMPLE_USER_NAME);
		fragmentEntryModel.setCreateDate(new Date());
		fragmentEntryModel.setModifiedDate(new Date());

		// Other fields

		fragmentEntryModel.setFragmentCollectionId(
			fragmentCollectionModel.getFragmentCollectionId());
		fragmentEntryModel.setFragmentEntryKey("web_content");
		fragmentEntryModel.setName("web_content");
		fragmentEntryModel.setCss(StringPool.BLANK);
		fragmentEntryModel.setHtml(_readFile("web_content.html"));
		fragmentEntryModel.setJs(StringPool.BLANK);
		fragmentEntryModel.setType(FragmentConstants.TYPE_COMPONENT);
		fragmentEntryModel.setStatus(WorkflowConstants.STATUS_APPROVED);

		// Autogenerated fields

		fragmentEntryModel.setUuid(SequentialUUID.generate());
		fragmentEntryModel.setHeadId(_counter.get());

		return fragmentEntryModel;
	}

	public FriendlyURLEntryLocalizationModel
		newFriendlyURLEntryLocalizationModel(
			FriendlyURLEntryModel friendlyURLEntryModel, String urlTitle) {

		FriendlyURLEntryLocalizationModel friendlyURLEntryLocalizationModel =
			new FriendlyURLEntryLocalizationModelImpl();

		// PK fields

		friendlyURLEntryLocalizationModel.setFriendlyURLEntryLocalizationId(
			_counter.get());

		// Group instance

		friendlyURLEntryLocalizationModel.setGroupId(
			friendlyURLEntryModel.getGroupId());

		// Audit fields

		friendlyURLEntryLocalizationModel.setCompanyId(
			friendlyURLEntryModel.getCompanyId());

		// Other fields

		friendlyURLEntryLocalizationModel.setClassNameId(
			friendlyURLEntryModel.getClassNameId());
		friendlyURLEntryLocalizationModel.setClassPK(
			friendlyURLEntryModel.getClassPK());
		friendlyURLEntryLocalizationModel.setUrlTitle(urlTitle);

		// Autogenerated fields

		friendlyURLEntryLocalizationModel.setFriendlyURLEntryId(
			friendlyURLEntryModel.getFriendlyURLEntryId());
		friendlyURLEntryLocalizationModel.setLanguageId(
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()));

		return friendlyURLEntryLocalizationModel;
	}

	public FriendlyURLEntryMappingModel newFriendlyURLEntryMapping(
		FriendlyURLEntryModel friendlyURLEntryModel) {

		FriendlyURLEntryMappingModel friendlyURLEntryMappingModel =
			new FriendlyURLEntryMappingModelImpl();

		// PK fields

		friendlyURLEntryMappingModel.setFriendlyURLEntryMappingId(
			_counter.get());

		//  Other fields

		friendlyURLEntryMappingModel.setClassNameId(
			friendlyURLEntryModel.getClassNameId());
		friendlyURLEntryMappingModel.setClassPK(
			friendlyURLEntryModel.getClassPK());
		friendlyURLEntryMappingModel.setFriendlyURLEntryId(
			friendlyURLEntryModel.getFriendlyURLEntryId());

		return friendlyURLEntryMappingModel;
	}

	public FriendlyURLEntryModel newFriendlyURLEntryModel(
		long groupId, long classNameId, long classPK) {

		FriendlyURLEntryModel friendlyURLEntryModel =
			new FriendlyURLEntryModelImpl();

		// PK fields

		friendlyURLEntryModel.setFriendlyURLEntryId(_counter.get());

		// Group instance

		friendlyURLEntryModel.setGroupId(groupId);

		// Audit fields

		friendlyURLEntryModel.setCompanyId(_companyId);
		friendlyURLEntryModel.setCreateDate(new Date());
		friendlyURLEntryModel.setModifiedDate(new Date());

		// Other fields

		friendlyURLEntryModel.setClassNameId(classNameId);
		friendlyURLEntryModel.setClassPK(classPK);

		// Autogenerated fields

		friendlyURLEntryModel.setUuid(SequentialUUID.generate());
		friendlyURLEntryModel.setDefaultLanguageId("en_US");

		return friendlyURLEntryModel;
	}

	public GroupModel newGlobalGroupModel() {
		_globalGroupId = _counter.get();

		return newGroupModel(
			getClassNameId(Company.class), _companyId, GroupConstants.GLOBAL,
			_globalGroupId, String.valueOf(_companyId), GroupConstants.GLOBAL,
			false, 0, StringPool.BLANK, _sampleUserId);
	}

	public List<LayoutModel> newGroupLayoutModels(long groupId) {
		List<LayoutModel> layoutModels = new ArrayList<>();

		if (BenchmarksPropsValues.MAX_BLOGS_ENTRY_COUNT != 0) {
			layoutModels.add(
				newLayoutModel(
					groupId, "blogs", "", BlogsPortletKeys.BLOGS + ","));
		}

		if (BenchmarksPropsValues.MAX_DL_FOLDER_COUNT != 0) {
			layoutModels.add(
				newLayoutModel(
					groupId, "document_library", "",
					DLPortletKeys.DOCUMENT_LIBRARY + ","));
		}

		if (BenchmarksPropsValues.MAX_MB_CATEGORY_COUNT != 0) {
			layoutModels.add(
				newLayoutModel(
					groupId, "forums", "", MBPortletKeys.MESSAGE_BOARDS + ","));
		}

		if (BenchmarksPropsValues.SEARCH_BAR_ENABLED) {
			layoutModels.add(newSearchLayoutModel(groupId, false));
		}

		return layoutModels;
	}

	public GroupModel newGroupModel(UserModel userModel) {
		return newGroupModel(
			getClassNameId(User.class), userModel.getUserId(), _counter.get(),
			userModel.getScreenName(), false);
	}

	public List<GroupModel> newGroupModels() {
		List<GroupModel> groupModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_GROUP_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_GROUP_COUNT; i++) {
			long groupId = _groupCounter.get();

			groupModels.add(
				newGroupModel(
					getClassNameId(Group.class), groupId, groupId, "Site " + i,
					true));
		}

		return groupModels;
	}

	public GroupModel newGuestGroupModel() {
		_guestGroupId = _counter.get();

		return newGroupModel(
			getClassNameId(Group.class), _guestGroupId, _guestGroupId,
			GroupConstants.GUEST, 0, "searchLayoutCreated=true", true);
	}

	public UserModel newGuestUserModel() {
		_guestUserId = _counter.get();

		return newUserModel(
			_guestUserId, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			UserConstants.TYPE_GUEST);
	}

	public JournalArticleLocalizationModel newJournalArticleLocalizationModel(
		JournalArticleModel journalArticleModel) {

		JournalArticleLocalizationModel journalArticleLocalizationModel =
			new JournalArticleLocalizationModelImpl();

		// PK fields

		journalArticleLocalizationModel.setArticleLocalizationId(
			_counter.get());

		// Audit fields

		journalArticleLocalizationModel.setCompanyId(
			journalArticleModel.getCompanyId());

		// Other fields

		journalArticleLocalizationModel.setArticlePK(
			journalArticleModel.getId());
		journalArticleLocalizationModel.setTitle(
			journalArticleModel.getUrlTitle());
		journalArticleLocalizationModel.setLanguageId(
			journalArticleModel.getDefaultLanguageId());

		return journalArticleLocalizationModel;
	}

	public JournalArticleModel newJournalArticleModel(
			JournalArticleResourceModel journalArticleResourceModel,
			int articleIndex, int versionIndex)
		throws PortalException {

		JournalArticleModel journalArticleModel = new JournalArticleModelImpl();

		// PK fields

		journalArticleModel.setId(_counter.get());

		// Resource

		journalArticleModel.setResourcePrimKey(
			journalArticleResourceModel.getResourcePrimKey());

		// Group instance

		journalArticleModel.setGroupId(
			journalArticleResourceModel.getGroupId());

		// Audit fields

		journalArticleModel.setCompanyId(_companyId);
		journalArticleModel.setUserId(_sampleUserId);
		journalArticleModel.setUserName(_SAMPLE_USER_NAME);
		journalArticleModel.setCreateDate(new Date());
		journalArticleModel.setModifiedDate(new Date());

		// Other fields

		journalArticleModel.setClassNameId(
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT);
		journalArticleModel.setTreePath("/");
		journalArticleModel.setArticleId(
			journalArticleResourceModel.getArticleId());
		journalArticleModel.setVersion(versionIndex);
		journalArticleModel.setUrlTitle(
			StringBundler.concat(
				"TestJournalArticle_", articleIndex, StringPool.UNDERLINE,
				versionIndex));
		journalArticleModel.setDDMStructureId(_defaultJournalDDMStructureId);
		journalArticleModel.setDDMTemplateKey(_JOURNAL_STRUCTURE_KEY);
		journalArticleModel.setDefaultLanguageId("en_US");
		journalArticleModel.setDisplayDate(new Date());
		journalArticleModel.setExpirationDate(nextFutureDate());
		journalArticleModel.setReviewDate(new Date());
		journalArticleModel.setIndexable(true);
		journalArticleModel.setLastPublishDate(new Date());
		journalArticleModel.setStatusDate(new Date());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		journalArticleModel.setUuid(uuid);
		journalArticleModel.setExternalReferenceCode(uuid);

		return journalArticleModel;
	}

	public List<PortletPreferenceValueModel>
		newJournalArticlePortletPreferenceValueModels(
			PortletPreferencesModel portletPreferencesModel,
			JournalArticleModel journalArticleModel, GroupModel groupModel) {

		return Arrays.asList(
			newPortletPreferenceValueModel(
				portletPreferencesModel, "articleExternalReferenceCode", 0,
				journalArticleModel.getUuid()),
			newPortletPreferenceValueModel(
				portletPreferencesModel, "assetEntryId", 0,
				String.valueOf(_journalArticleAssetEntryModel.getEntryId())),
			newPortletPreferenceValueModel(
				portletPreferencesModel, "contentMetadataAssetAddonEntryKeys",
				0, "false"),
			newPortletPreferenceValueModel(
				portletPreferencesModel, "ddmTemplateExternalReferenceCode", 0,
				StringPool.BLANK),
			newPortletPreferenceValueModel(
				portletPreferencesModel, "enableViewCountIncrement", 0,
				"false"),
			newPortletPreferenceValueModel(
				portletPreferencesModel, "groupExternalReferenceCode", 0,
				groupModel.getUuid()),
			newPortletPreferenceValueModel(
				portletPreferencesModel, "userToolAssetAddonEntryKeys", 0,
				"false"));
	}

	public JournalArticleResourceModel newJournalArticleResourceModel(
		long groupId) {

		JournalArticleResourceModel journalArticleResourceModel =
			new JournalArticleResourceModelImpl();

		// PK fields

		journalArticleResourceModel.setResourcePrimKey(_counter.get());

		// Group instance

		journalArticleResourceModel.setGroupId(groupId);

		// Audit fields

		journalArticleResourceModel.setCompanyId(_companyId);

		// Other fields

		journalArticleResourceModel.setArticleId(
			String.valueOf(_counter.get()));

		// Autogenerated fields

		journalArticleResourceModel.setUuid(SequentialUUID.generate());

		_journalArticleResourceUUIDs.put(
			journalArticleResourceModel.getPrimaryKey(),
			journalArticleResourceModel.getUuid());

		return journalArticleResourceModel;
	}

	public PortletPreferencesModel newJournalContentPortletPreferencesModel(
		FragmentEntryLinkModel fragmentEntryLinkModel) {

		PortletPreferencesModel portletPreferencesModel =
			new PortletPreferencesModelImpl();

		// PK fields

		portletPreferencesModel.setPortletPreferencesId(_counter.get());

		// Other fields

		portletPreferencesModel.setOwnerId(PortletKeys.PREFS_OWNER_ID_DEFAULT);
		portletPreferencesModel.setOwnerType(
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT);
		portletPreferencesModel.setPlid(fragmentEntryLinkModel.getClassPK());
		portletPreferencesModel.setPortletId(
			PortletIdCodec.encode(
				JournalContentPortletKeys.JOURNAL_CONTENT,
				fragmentEntryLinkModel.getNamespace()));

		return portletPreferencesModel;
	}

	public PortletPreferenceValueModel
		newJournalContentPortletPreferenceValueModel(
			PortletPreferencesModel portletPreferencesModel,
			JournalArticleModel journalArticleModel) {

		return newPortletPreferenceValueModel(
			portletPreferencesModel, "articleId", 0,
			journalArticleModel.getArticleId());
	}

	public LayoutClassedModelUsageModel newLayoutClassedModelUsageModel(
		long groupId, long plid, String containerKey,
		JournalArticleResourceModel journalArticleResourceModel) {

		LayoutClassedModelUsageModel layoutClassedModelUsageModel =
			new LayoutClassedModelUsageModelImpl();

		// PK fields

		layoutClassedModelUsageModel.setLayoutClassedModelUsageId(
			_counter.get());

		// Group instance

		layoutClassedModelUsageModel.setGroupId(groupId);

		// Audit fields

		layoutClassedModelUsageModel.setCompanyId(_companyId);
		layoutClassedModelUsageModel.setCreateDate(new Date());
		layoutClassedModelUsageModel.setModifiedDate(new Date());

		// Other fields

		layoutClassedModelUsageModel.setClassNameId(
			getClassNameId(JournalArticle.class));
		layoutClassedModelUsageModel.setClassPK(
			journalArticleResourceModel.getResourcePrimKey());
		layoutClassedModelUsageModel.setClassedModelExternalReferenceCode(
			StringPool.BLANK);
		layoutClassedModelUsageModel.setContainerKey(containerKey);
		layoutClassedModelUsageModel.setContainerType(
			getClassNameId(Portlet.class));
		layoutClassedModelUsageModel.setPlid(plid);
		layoutClassedModelUsageModel.setType(
			LayoutClassedModelUsageConstants.TYPE_LAYOUT);
		layoutClassedModelUsageModel.setLastPublishDate(new Date());

		// Autogenerated fields

		layoutClassedModelUsageModel.setUuid(SequentialUUID.generate());

		return layoutClassedModelUsageModel;
	}

	public LayoutFriendlyURLModel newLayoutFriendlyURLModel(
		LayoutModel layoutModel) {

		LayoutFriendlyURLModel layoutFriendlyURLEntryModel =
			new LayoutFriendlyURLModelImpl();

		// PK fields

		layoutFriendlyURLEntryModel.setLayoutFriendlyURLId(_counter.get());

		// Group instance

		layoutFriendlyURLEntryModel.setGroupId(layoutModel.getGroupId());

		// Audit fields

		layoutFriendlyURLEntryModel.setCompanyId(_companyId);
		layoutFriendlyURLEntryModel.setUserId(_sampleUserId);
		layoutFriendlyURLEntryModel.setUserName(_SAMPLE_USER_NAME);
		layoutFriendlyURLEntryModel.setCreateDate(new Date());
		layoutFriendlyURLEntryModel.setModifiedDate(new Date());

		// Other fields

		layoutFriendlyURLEntryModel.setPlid(layoutModel.getPlid());
		layoutFriendlyURLEntryModel.setPrivateLayout(
			layoutModel.getPrivateLayout());
		layoutFriendlyURLEntryModel.setFriendlyURL(
			layoutModel.getFriendlyURL());
		layoutFriendlyURLEntryModel.setLanguageId("en_US");
		layoutFriendlyURLEntryModel.setLastPublishDate(new Date());

		// Autogenerated fields

		layoutFriendlyURLEntryModel.setUuid(SequentialUUID.generate());

		return layoutFriendlyURLEntryModel;
	}

	public LayoutModel newLayoutModel(
		long groupId, String name, String column1, String column2) {

		return newLayoutModel(
			groupId, "2_columns_ii", name, false, column1, column2);
	}

	public List<LayoutModel> newLayoutModels(
		long groupId, String name, String column1, String column2) {

		return ListUtil.fromArray(
			newLayoutModel(
				groupId, "2_columns_ii", name, false, column1, column2),
			newLayoutModel(
				groupId, "2_columns_ii", name, true, column1, column2));
	}

	public LayoutPageTemplateStructureModel newLayoutPageTemplateStructureModel(
		LayoutModel layoutModel) {

		LayoutPageTemplateStructureModel layoutPageTemplateStructureModel =
			new LayoutPageTemplateStructureModelImpl();

		// PK fields

		layoutPageTemplateStructureModel.setLayoutPageTemplateStructureId(
			_counter.get());

		// Group instance

		layoutPageTemplateStructureModel.setGroupId(layoutModel.getGroupId());

		// Audit fields

		layoutPageTemplateStructureModel.setCompanyId(_companyId);
		layoutPageTemplateStructureModel.setUserId(_sampleUserId);
		layoutPageTemplateStructureModel.setUserName(_SAMPLE_USER_NAME);
		layoutPageTemplateStructureModel.setCreateDate(new Date());
		layoutPageTemplateStructureModel.setModifiedDate(new Date());

		// Other fields

		layoutPageTemplateStructureModel.setPlid(layoutModel.getPlid());

		// Autogenerated fields

		layoutPageTemplateStructureModel.setUuid(SequentialUUID.generate());

		return layoutPageTemplateStructureModel;
	}

	public LayoutPageTemplateStructureRelModel
			newLayoutPageTemplateStructureRelModel(
				LayoutModel layoutModel,
				LayoutPageTemplateStructureModel
					layoutPageTemplateStructureModel,
				FragmentEntryLinkModel fragmentEntryLinkModel)
		throws Exception {

		LayoutPageTemplateStructureRelModel
			layoutPageTemplateStructureRelModel =
				new LayoutPageTemplateStructureRelModelImpl();

		// PK fields

		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureRelId(
			_counter.get());

		// Group instance

		layoutPageTemplateStructureRelModel.setGroupId(
			layoutPageTemplateStructureModel.getGroupId());

		// Audit fields

		layoutPageTemplateStructureRelModel.setCompanyId(_companyId);
		layoutPageTemplateStructureRelModel.setUserId(_sampleUserId);
		layoutPageTemplateStructureRelModel.setUserName(_SAMPLE_USER_NAME);
		layoutPageTemplateStructureRelModel.setCreateDate(new Date());
		layoutPageTemplateStructureRelModel.setModifiedDate(new Date());

		// Other fields

		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureId(
			layoutPageTemplateStructureModel.
				getLayoutPageTemplateStructureId());
		layoutPageTemplateStructureRelModel.setSegmentsExperienceId(0L);
		layoutPageTemplateStructureRelModel.setData(
			StringUtil.replace(
				_layoutPageTemplateStructureRelData, "${fragmentEntryLinkId}",
				String.valueOf(
					fragmentEntryLinkModel.getFragmentEntryLinkId())));

		// Autogenerated fields

		layoutPageTemplateStructureRelModel.setUuid(SequentialUUID.generate());

		return layoutPageTemplateStructureRelModel;
	}

	public LayoutPageTemplateStructureRelModel
			newLayoutPageTemplateStructureRelModel(
				LayoutModel layoutModel,
				LayoutPageTemplateStructureModel
					layoutPageTemplateStructureModel,
				List<FragmentEntryLinkModel> fragmentEntryLinkModels)
		throws Exception {

		LayoutPageTemplateStructureRelModel
			layoutPageTemplateStructureRelModel =
				new LayoutPageTemplateStructureRelModelImpl();

		// PK fields

		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureRelId(
			_counter.get());

		// Group instance

		layoutPageTemplateStructureRelModel.setGroupId(
			layoutPageTemplateStructureModel.getGroupId());

		// Audit fields

		layoutPageTemplateStructureRelModel.setCompanyId(_companyId);
		layoutPageTemplateStructureRelModel.setUserId(_sampleUserId);
		layoutPageTemplateStructureRelModel.setUserName(_SAMPLE_USER_NAME);
		layoutPageTemplateStructureRelModel.setCreateDate(new Date());
		layoutPageTemplateStructureRelModel.setModifiedDate(new Date());

		// Other fields

		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureId(
			layoutPageTemplateStructureModel.
				getLayoutPageTemplateStructureId());

		FragmentEntryLinkModel fragmentEntryLinkModel =
			fragmentEntryLinkModels.get(0);

		layoutPageTemplateStructureRelModel.setSegmentsExperienceId(
			fragmentEntryLinkModel.getSegmentsExperienceId());

		layoutPageTemplateStructureRelModel.setData(
			_generateData(fragmentEntryLinkModels));
		layoutPageTemplateStructureRelModel.setStatusByUserId(_sampleUserId);
		layoutPageTemplateStructureRelModel.setStatusByUserName(
			_SAMPLE_USER_NAME);
		layoutPageTemplateStructureRelModel.setStatusDate(new Date());

		// Autogenerated fields

		layoutPageTemplateStructureRelModel.setUuid(SequentialUUID.generate());

		return layoutPageTemplateStructureRelModel;
	}

	public LayoutPageTemplateStructureRelModel
			newLayoutPageTemplateStructureRelModel(
				LayoutModel layoutModel,
				LayoutPageTemplateStructureModel
					layoutPageTemplateStructureModel,
				List<FragmentEntryLinkModel> fragmentEntryLinkModels,
				String templateFileName)
		throws Exception {

		List<FragmentEntryLinkModel> targetFragmentEntryLinkModels =
			new ArrayList<>();

		for (FragmentEntryLinkModel model : fragmentEntryLinkModels) {
			if (model.getPlid() == layoutModel.getPlid()) {
				targetFragmentEntryLinkModels.add(model);
			}
		}

		LayoutPageTemplateStructureRelModel
			layoutPageTemplateStructureRelModel =
				new LayoutPageTemplateStructureRelModelImpl();

		// PK fields

		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureRelId(
			_counter.get());

		// Group instance

		layoutPageTemplateStructureRelModel.setGroupId(
			layoutPageTemplateStructureModel.getGroupId());

		// Audit fields

		layoutPageTemplateStructureRelModel.setCompanyId(_companyId);
		layoutPageTemplateStructureRelModel.setUserId(_sampleUserId);
		layoutPageTemplateStructureRelModel.setUserName(_SAMPLE_USER_NAME);
		layoutPageTemplateStructureRelModel.setCreateDate(new Date());
		layoutPageTemplateStructureRelModel.setModifiedDate(new Date());

		// Other fields

		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureId(
			layoutPageTemplateStructureModel.
				getLayoutPageTemplateStructureId());

		FragmentEntryLinkModel fragmentEntryLinkModel =
			targetFragmentEntryLinkModels.get(0);

		layoutPageTemplateStructureRelModel.setSegmentsExperienceId(
			fragmentEntryLinkModel.getSegmentsExperienceId());

		layoutPageTemplateStructureRelModel.setData(
			_generateData(targetFragmentEntryLinkModels, templateFileName));
		layoutPageTemplateStructureRelModel.setStatusByUserId(_sampleUserId);
		layoutPageTemplateStructureRelModel.setStatusByUserName(
			_SAMPLE_USER_NAME);
		layoutPageTemplateStructureRelModel.setStatusDate(new Date());

		// Autogenerated fields

		layoutPageTemplateStructureRelModel.setUuid(SequentialUUID.generate());

		return layoutPageTemplateStructureRelModel;
	}

	public LayoutPrototypeModel newLayoutPrototypeModel(long userId) {
		LayoutPrototypeModel layoutPrototypeModel =
			new LayoutPrototypeModelImpl();

		// PK fields

		layoutPrototypeModel.setLayoutPrototypeId(_counter.get());

		// Audit fields

		layoutPrototypeModel.setCompanyId(_companyId);
		layoutPrototypeModel.setUserId(userId);
		layoutPrototypeModel.setCreateDate(new Date());
		layoutPrototypeModel.setModifiedDate(new Date());

		// Other fields

		layoutPrototypeModel.setName(
			"<?xml version=\"1.0\"?><root><name>Search</name></root>");
		layoutPrototypeModel.setDescription(
			"<?xml version=\"1.0\"?><root><Description>Display search " +
				"results with a default set of facets.</Description></root>");
		layoutPrototypeModel.setActive(true);

		// Autogenerated fields

		layoutPrototypeModel.setUuid(SequentialUUID.generate());

		return layoutPrototypeModel;
	}

	public List<LayoutSetModel> newLayoutSetModels(long groupId) {
		return newLayoutSetModels(groupId, "classic_WAR_classictheme");
	}

	public List<LayoutSetModel> newLayoutSetModels(
		long groupId, String themeId) {

		return ListUtil.fromArray(
			newLayoutSetModel(groupId, true, themeId),
			newLayoutSetModel(groupId, false, themeId));
	}

	public ListTypeDefinitionModel newListTypeDefinitionModel() {
		ListTypeDefinitionModel listTypeDefinitionModel =
			new ListTypeDefinitionModelImpl();

		// PK fields

		listTypeDefinitionModel.setListTypeDefinitionId(_counter.get());

		// Audit fields

		listTypeDefinitionModel.setCompanyId(_companyId);
		listTypeDefinitionModel.setUserId(_sampleUserId);
		listTypeDefinitionModel.setUserName(_SAMPLE_USER_NAME);
		listTypeDefinitionModel.setCreateDate(new Date());
		listTypeDefinitionModel.setModifiedDate(new Date());

		// Other field

		listTypeDefinitionModel.setName(
			StringBundler.concat(
				"<?xml version=\"1.0\"?><root available-locales=\"en_US\" ",
				"default-locale=\"en_US\"><name language-id=\"en_US\">",
				"Picklist ", listTypeDefinitionModel.getListTypeDefinitionId(),
				"</name></root>"));

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		listTypeDefinitionModel.setUuid(uuid);
		listTypeDefinitionModel.setExternalReferenceCode(uuid);

		return listTypeDefinitionModel;
	}

	public List<ListTypeDefinitionModel> newListTypeDefinitionModels() {
		List<ListTypeDefinitionModel> listTypeDefinitionModels =
			new ArrayList<>(
				BenchmarksPropsValues.MAX_LIST_TYPE_DEFINITION_COUNT);

		for (int i = 1;
			 i <= BenchmarksPropsValues.MAX_LIST_TYPE_DEFINITION_COUNT; i++) {

			listTypeDefinitionModels.add(newListTypeDefinitionModel());
		}

		return listTypeDefinitionModels;
	}

	public List<ListTypeEntryModel> newListTypeEntryModels(
		long listTypeDefinitionId) {

		List<ListTypeEntryModel> listTypeEntryModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_LIST_TYPE_ENTRY_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_LIST_TYPE_ENTRY_COUNT;
			 i++) {

			listTypeEntryModels.add(
				newListTypeEntryModel(listTypeDefinitionId));
		}

		return listTypeEntryModels;
	}

	public List<MBCategoryModel> newMBCategoryModels(long groupId) {
		List<MBCategoryModel> mbCategoryModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_MB_CATEGORY_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_MB_CATEGORY_COUNT; i++) {
			mbCategoryModels.add(newMBCategoryModel(groupId, i));
		}

		return mbCategoryModels;
	}

	public MBDiscussionModel newMBDiscussionModel(
		long groupId, long classNameId, long classPK, long threadId) {

		MBDiscussionModel mbDiscussionModel = new MBDiscussionModelImpl();

		// PK fields

		mbDiscussionModel.setDiscussionId(_counter.get());

		// Group instance

		mbDiscussionModel.setGroupId(groupId);

		// Audit fields

		mbDiscussionModel.setCompanyId(_companyId);
		mbDiscussionModel.setUserId(_sampleUserId);
		mbDiscussionModel.setUserName(_SAMPLE_USER_NAME);
		mbDiscussionModel.setCreateDate(new Date());
		mbDiscussionModel.setModifiedDate(new Date());

		// Other fields

		mbDiscussionModel.setClassNameId(classNameId);
		mbDiscussionModel.setClassPK(classPK);
		mbDiscussionModel.setThreadId(threadId);
		mbDiscussionModel.setLastPublishDate(new Date());

		// Autogenerated fields

		mbDiscussionModel.setUuid(SequentialUUID.generate());

		return mbDiscussionModel;
	}

	public MBMailingListModel newMBMailingListModel(
		MBCategoryModel mbCategoryModel, UserModel sampleUserModel) {

		MBMailingListModel mbMailingListModel = new MBMailingListModelImpl();

		// PK fields

		mbMailingListModel.setMailingListId(_counter.get());

		// Group instance

		mbMailingListModel.setGroupId(mbCategoryModel.getGroupId());

		// Audit fields

		mbMailingListModel.setCompanyId(_companyId);
		mbMailingListModel.setUserId(_sampleUserId);
		mbMailingListModel.setUserName(_SAMPLE_USER_NAME);
		mbMailingListModel.setCreateDate(new Date());
		mbMailingListModel.setModifiedDate(new Date());

		// Other fields

		mbMailingListModel.setCategoryId(mbCategoryModel.getCategoryId());
		mbMailingListModel.setInProtocol("pop3");
		mbMailingListModel.setInServerPort(110);
		mbMailingListModel.setInUserName(sampleUserModel.getEmailAddress());
		mbMailingListModel.setInPassword(sampleUserModel.getPassword());
		mbMailingListModel.setInReadInterval(5);
		mbMailingListModel.setOutServerPort(25);

		// Autogenerated fields

		mbMailingListModel.setUuid(SequentialUUID.generate());

		return mbMailingListModel;
	}

	public MBMessageModel newMBMessageModel(
		MBThreadModel mbThreadModel, long classNameId, long classPK,
		int index) {

		long messageId = 0;
		long parentMessageId = 0;
		String subject = null;
		String body = null;
		String urlSubject = null;

		if (index == 0) {
			messageId = mbThreadModel.getRootMessageId();
			parentMessageId = MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID;
			subject = String.valueOf(classPK);
			body = String.valueOf(classPK);
			urlSubject = String.valueOf(mbThreadModel.getRootMessageId());
		}
		else {
			messageId = _counter.get();
			parentMessageId = mbThreadModel.getRootMessageId();
			subject = "N/A";
			body = "This is test comment " + index + ".";
			urlSubject = "test-comment-" + index;
		}

		return newMBMessageModel(
			mbThreadModel.getGroupId(), classNameId, classPK,
			MBCategoryConstants.DISCUSSION_CATEGORY_ID,
			mbThreadModel.getThreadId(), messageId,
			mbThreadModel.getRootMessageId(), parentMessageId, subject,
			urlSubject, body);
	}

	public List<MBMessageModel> newMBMessageModels(
		MBThreadModel mbThreadModel) {

		List<MBMessageModel> mbMessageModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_MB_MESSAGE_COUNT);

		mbMessageModels.add(
			newMBMessageModel(
				mbThreadModel.getGroupId(), 0, 0, mbThreadModel.getCategoryId(),
				mbThreadModel.getThreadId(), mbThreadModel.getRootMessageId(),
				mbThreadModel.getRootMessageId(),
				MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID, "Test Message 1",
				"test-message-1", "This is test message 1."));

		for (int i = 2; i <= BenchmarksPropsValues.MAX_MB_MESSAGE_COUNT; i++) {
			mbMessageModels.add(
				newMBMessageModel(
					mbThreadModel.getGroupId(), 0, 0,
					mbThreadModel.getCategoryId(), mbThreadModel.getThreadId(),
					_counter.get(), mbThreadModel.getRootMessageId(),
					mbThreadModel.getRootMessageId(), "Test Message " + i,
					"test-message-" + i, "This is test message " + i + "."));
		}

		return mbMessageModels;
	}

	public List<MBMessageModel> newMBMessageModels(
		MBThreadModel mbThreadModel, long classNameId, long classPK,
		int maxMessageCount) {

		List<MBMessageModel> mbMessageModels = new ArrayList<>(maxMessageCount);

		for (int i = 1; i <= maxMessageCount; i++) {
			mbMessageModels.add(
				newMBMessageModel(mbThreadModel, classNameId, classPK, i));
		}

		return mbMessageModels;
	}

	public MBThreadFlagModel newMBThreadFlagModel(MBThreadModel mbThreadModel) {
		MBThreadFlagModel mbThreadFlagModel = new MBThreadFlagModelImpl();

		// PK fields

		mbThreadFlagModel.setThreadFlagId(_counter.get());

		// Group instance

		mbThreadFlagModel.setGroupId(mbThreadModel.getGroupId());

		// Audit fields

		mbThreadFlagModel.setCompanyId(_companyId);
		mbThreadFlagModel.setUserId(_sampleUserId);
		mbThreadFlagModel.setUserName(_SAMPLE_USER_NAME);
		mbThreadFlagModel.setCreateDate(new Date());
		mbThreadFlagModel.setModifiedDate(new Date());

		// Other fields

		mbThreadFlagModel.setThreadId(mbThreadModel.getThreadId());
		mbThreadFlagModel.setLastPublishDate(new Date());

		// Autogenerated fields

		mbThreadFlagModel.setUuid(SequentialUUID.generate());

		return mbThreadFlagModel;
	}

	public MBThreadModel newMBThreadModel(
		long threadId, long groupId, long rootMessageId) {

		return newMBThreadModel(
			threadId, groupId, MBCategoryConstants.DISCUSSION_CATEGORY_ID,
			rootMessageId);
	}

	public List<MBThreadModel> newMBThreadModels(
		MBCategoryModel mbCategoryModel) {

		List<MBThreadModel> mbThreadModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_MB_THREAD_COUNT);

		for (int i = 0; i < BenchmarksPropsValues.MAX_MB_THREAD_COUNT; i++) {
			mbThreadModels.add(
				newMBThreadModel(
					_counter.get(), mbCategoryModel.getGroupId(),
					mbCategoryModel.getCategoryId(), _counter.get()));
		}

		return mbThreadModels;
	}

	public NotificationTemplateModel newNotificationTemplateModel()
		throws Exception {

		NotificationTemplateModel notificationTemplateModel =
			new NotificationTemplateModelImpl();

		// PK fields

		notificationTemplateModel.setNotificationTemplateId(_counter.get());

		// Audit fields

		notificationTemplateModel.setCompanyId(_companyId);
		notificationTemplateModel.setUserId(_sampleUserId);
		notificationTemplateModel.setUserName(_SAMPLE_USER_NAME);
		notificationTemplateModel.setCreateDate(new Date());
		notificationTemplateModel.setModifiedDate(new Date());

		// Other field

		notificationTemplateModel.setObjectDefinitionId(0);
		notificationTemplateModel.setEditorType(
			NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT);
		notificationTemplateModel.setName("Commerce Order Notification");
		notificationTemplateModel.setRecipientType("email");
		notificationTemplateModel.setSubject(
			StringUtil.read(
				getResourceInputStream("notification_template/subject.xml")));
		notificationTemplateModel.setSystem(false);
		notificationTemplateModel.setType("email");

		// Autogenerated fields

		notificationTemplateModel.setUuid(SequentialUUID.generate());
		notificationTemplateModel.setExternalReferenceCode(
			"L_COMMERCE_ORDER_TEMPLATE");

		return notificationTemplateModel;
	}

	public ObjectActionModel newObjectActionModel(
		long objectDefinitionId, long notificationTemplateId) {

		ObjectActionModel objectActionModel = new ObjectActionModelImpl();

		// PK fields

		objectActionModel.setObjectActionId(_counter.get());

		// Audit fields

		objectActionModel.setCompanyId(_companyId);
		objectActionModel.setUserId(_sampleUserId);
		objectActionModel.setUserName(_SAMPLE_USER_NAME);
		objectActionModel.setCreateDate(new Date());
		objectActionModel.setModifiedDate(new Date());

		// Other field

		objectActionModel.setObjectDefinitionId(objectDefinitionId);
		objectActionModel.setActive(false);
		objectActionModel.setConditionExpression("orderStatus == 1");
		objectActionModel.setLabel(
			StringBundler.concat(
				"<?xml version=\"1.0\"?><root available-locales=\"en_US\" ",
				"default-locale=\"en_US\"><Label language-id=\"en_US\">",
				"Commerce Order Notification</Label></root>"));
		objectActionModel.setName("commerceOrderNotification");
		objectActionModel.setObjectActionExecutorKey("notification");
		objectActionModel.setObjectActionTriggerKey(
			"liferay/commerce_order_status");
		objectActionModel.setParameters(
			"notificationTemplateId=" + notificationTemplateId);
		objectActionModel.setSystem(false);
		objectActionModel.setStatus(ObjectActionConstants.STATUS_NEVER_RAN);

		// Autogenerated fields

		objectActionModel.setUuid(SequentialUUID.generate());
		objectActionModel.setExternalReferenceCode(
			"L_COMMERCE_ORDER_NOTIFICATION");

		return objectActionModel;
	}

	public LayoutPageTemplateStructureRelModel
			newObjectDefinitionLayoutPageTemplateStructureRelModel(
				List<FragmentEntryLinkModel> fragmentEntryLinkModels,
				LayoutPageTemplateStructureModel
					layoutPageTemplateStructureModel,
				ObjectDefinition objectDefinition)
		throws Exception {

		LayoutPageTemplateStructureRelModel
			layoutPageTemplateStructureRelModel =
				new LayoutPageTemplateStructureRelModelImpl();

		// PK fields

		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureRelId(
			_counter.get());

		// Group instance

		layoutPageTemplateStructureRelModel.setGroupId(
			layoutPageTemplateStructureModel.getGroupId());

		// Audit fields

		layoutPageTemplateStructureRelModel.setCompanyId(_companyId);
		layoutPageTemplateStructureRelModel.setUserId(_sampleUserId);
		layoutPageTemplateStructureRelModel.setUserName(_SAMPLE_USER_NAME);
		layoutPageTemplateStructureRelModel.setCreateDate(new Date());
		layoutPageTemplateStructureRelModel.setModifiedDate(new Date());

		// Other fields

		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureId(
			layoutPageTemplateStructureModel.
				getLayoutPageTemplateStructureId());

		String data = _readFile(
			"object/object_definition_layout_page_template_structure_rel.json");

		data = StringUtil.replace(
			data, "${objectDefinitionClassName}",
			objectDefinition.getClassName());

		for (FragmentEntryLinkModel fragmentEntryLinkModel :
				fragmentEntryLinkModels) {

			data = StringUtil.replaceFirst(
				data, "${fragmentEntryLinkId}",
				String.valueOf(
					fragmentEntryLinkModel.getFragmentEntryLinkId()));
		}

		FragmentEntryLinkModel fragmentEntryLinkModel =
			fragmentEntryLinkModels.get(0);

		layoutPageTemplateStructureRelModel.setSegmentsExperienceId(
			fragmentEntryLinkModel.getSegmentsExperienceId());

		layoutPageTemplateStructureRelModel.setData(data);

		// Autogenerated fields

		layoutPageTemplateStructureRelModel.setUuid(SequentialUUID.generate());

		return layoutPageTemplateStructureRelModel;
	}

	public ObjectDefinitionModel newObjectDefinitionModel(
		long objectFolderId, String name) {

		long objectDefinitionId = _counter.get();

		String className =
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION + objectDefinitionId;

		ClassNameModel classNameModel = new ClassNameModelImpl();

		classNameModel.setClassNameId(_counter.get());
		classNameModel.setValue(className);

		_classNameModels.put(className, classNameModel);

		String label = _getObjectLabel(name);

		String uuid = SequentialUUID.generate();

		return newObjectDefinitionModel(
			objectDefinitionId, objectFolderId, 0, className,
			StringBundler.concat("O_", _companyId, StringPool.UNDERLINE, name),
			true, false, true, label, true, name,
			PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS_CUSTOM_APPS,
			"c_" + StringUtil.toLowerCase(name) + "_",
			"c_" + StringUtil.toLowerCase(name), label, true, false, uuid,
			uuid);
	}

	public List<ObjectDefinitionModel> newObjectDefinitionModels(
		long objectFolderId) {

		return ListUtil.fromArray(
			newObjectDefinitionModel(
				_counter.get(), objectFolderId, _counter.get(),
				CommerceOrder.class.getName(), "CommerceOrder", false, true,
				false, _getObjectLabel("Commerce Order"), false,
				"CommerceOrder", null, "commerceOrderId", "commerceOrderId",
				_getObjectPluralLabel("Commerce Orders"), false, true,
				"L_COMMERCE_ORDER", SequentialUUID.generate()),
			newObjectDefinitionModel(
				_counter.get(), objectFolderId, _counter.get(),
				User.class.getName(), "User_", false, true, false,
				_getObjectLabel("User"), false, "User", null, "userId",
				"userId", _getObjectPluralLabel("Users"), false, true, "L_USER",
				SequentialUUID.generate()));
	}

	public List<ObjectEntryModel> newObjectEntryModels(
		long objectDefinitionId) {

		List<ObjectEntryModel> objectEntryModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_OBJECT_ENTRY_COUNT);

		for (int i = 0; i < BenchmarksPropsValues.MAX_OBJECT_ENTRY_COUNT; i++) {
			objectEntryModels.add(newObjectEntryModel(objectDefinitionId));
		}

		return objectEntryModels;
	}

	public ObjectFieldModel newObjectFieldModel(
		long listTypeDefinitionId, long objectDefinitionId, String businessType,
		String dbColumnName, String dbTableName, String dbType, String label,
		String name, boolean required, boolean state, boolean system) {

		ObjectFieldModel objectFieldModel = new ObjectFieldImpl();

		// PK fields

		objectFieldModel.setObjectFieldId(_counter.get());

		if (StringUtil.equals(
				businessType,
				ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

			_objectFieldId = objectFieldModel.getObjectFieldId();
		}

		// Audit fields

		objectFieldModel.setCompanyId(_companyId);
		objectFieldModel.setUserId(_sampleUserId);
		objectFieldModel.setUserName(_SAMPLE_USER_NAME);
		objectFieldModel.setCreateDate(new Date());
		objectFieldModel.setModifiedDate(new Date());

		// Other fields

		objectFieldModel.setListTypeDefinitionId(listTypeDefinitionId);
		objectFieldModel.setObjectDefinitionId(objectDefinitionId);
		objectFieldModel.setBusinessType(businessType);
		objectFieldModel.setDBColumnName(dbColumnName);
		objectFieldModel.setDBTableName(dbTableName);
		objectFieldModel.setDBType(dbType);
		objectFieldModel.setIndexedAsKeyword(name.equals("id"));
		objectFieldModel.setLabel(_getObjectLabel(label));
		objectFieldModel.setName(name);
		objectFieldModel.setReadOnly(String.valueOf(system));

		if (businessType.equals(
				ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

			objectFieldModel.setRelationshipType(
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		}

		objectFieldModel.setRequired(required);
		objectFieldModel.setState(state);
		objectFieldModel.setSystem(system);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		objectFieldModel.setUuid(uuid);
		objectFieldModel.setExternalReferenceCode(uuid);

		return objectFieldModel;
	}

	public List<ObjectFieldModel> newObjectFieldModels(
		long objectDefinitionId, String dbTableName, long listTypeDefinitionId,
		String... dbColumnNames) {

		if (dbColumnNames.length != 1) {
			return ListUtil.fromArray(
				newObjectFieldModel(
					0, objectDefinitionId,
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
					"attachment_", dbTableName,
					ObjectFieldConstants.DB_TYPE_LONG, "Attachment",
					"attachment", false, false, false),
				newObjectFieldModel(
					0, objectDefinitionId,
					ObjectFieldConstants.BUSINESS_TYPE_DATE, "createDate",
					"ObjectEntry", ObjectFieldConstants.DB_TYPE_DATE,
					"Create Date", "createDate", false, false, true),
				newObjectFieldModel(
					0, objectDefinitionId,
					ObjectFieldConstants.BUSINESS_TYPE_TEXT, "userName",
					"ObjectEntry", ObjectFieldConstants.DB_TYPE_STRING,
					"Author", "creator", false, false, true),
				newObjectFieldModel(
					0, objectDefinitionId,
					ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT,
					"description_", dbTableName,
					ObjectFieldConstants.DB_TYPE_CLOB, "Description",
					"description", true, false, false),
				newObjectFieldModel(
					0, objectDefinitionId,
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					"externalReferenceCode", "ObjectEntry",
					ObjectFieldConstants.DB_TYPE_STRING,
					"External Reference Code", "externalReferenceCode", false,
					false, true),
				newObjectFieldModel(
					0, objectDefinitionId,
					ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER,
					"objectEntryId", "ObjectEntry",
					ObjectFieldConstants.DB_TYPE_LONG, "ID", "id", false, false,
					true),
				newObjectFieldModel(
					0, objectDefinitionId,
					ObjectFieldConstants.BUSINESS_TYPE_DATE, "modifiedDate",
					"ObjectEntry", ObjectFieldConstants.DB_TYPE_DATE,
					"Modified Date", "modifiedDate", false, false, true),
				newObjectFieldModel(
					0, objectDefinitionId,
					ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP,
					"r_userTicket_userId", dbTableName,
					ObjectFieldConstants.DB_TYPE_LONG, "Assignee",
					"r_userTicket_userId", false, false, false),
				newObjectFieldModel(
					0, objectDefinitionId,
					ObjectFieldConstants.BUSINESS_TYPE_TEXT, "status",
					"ObjectEntry", ObjectFieldConstants.DB_TYPE_STRING,
					"Status", "status", false, false, true),
				newObjectFieldModel(
					0, objectDefinitionId,
					ObjectFieldConstants.BUSINESS_TYPE_TEXT, "subject_",
					dbTableName, ObjectFieldConstants.DB_TYPE_STRING, "Subject",
					"subject", true, false, false),
				newObjectFieldModel(
					listTypeDefinitionId, objectDefinitionId,
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST, "supportType_",
					dbTableName, ObjectFieldConstants.DB_TYPE_STRING,
					"Support type", "supportType", true, false, false),
				newObjectFieldModel(
					listTypeDefinitionId, objectDefinitionId,
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST,
					"ticketStatus_", dbTableName,
					ObjectFieldConstants.DB_TYPE_STRING, "Ticket Status",
					"ticketStatus", true, true, false));
		}

		return ListUtil.fromArray(
			newObjectFieldModel(
				listTypeDefinitionId, objectDefinitionId,
				ObjectFieldConstants.BUSINESS_TYPE_DATE, "createDate",
				dbTableName, ObjectFieldConstants.DB_TYPE_DATE, "Create Date",
				"createDate", false, false, true),
			newObjectFieldModel(
				listTypeDefinitionId, objectDefinitionId,
				ObjectFieldConstants.BUSINESS_TYPE_TEXT, "userName",
				dbTableName, ObjectFieldConstants.DB_TYPE_STRING, "Author",
				"creator", false, false, true),
			newObjectFieldModel(
				listTypeDefinitionId, objectDefinitionId,
				ObjectFieldConstants.BUSINESS_TYPE_TEXT,
				"externalReferenceCode", dbTableName,
				ObjectFieldConstants.DB_TYPE_STRING, "External Reference Code",
				"externalReferenceCode", false, false, true),
			newObjectFieldModel(
				listTypeDefinitionId, objectDefinitionId,
				ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER,
				dbColumnNames[0], dbTableName,
				ObjectFieldConstants.DB_TYPE_LONG, "ID", "id", false, false,
				true),
			newObjectFieldModel(
				listTypeDefinitionId, objectDefinitionId,
				ObjectFieldConstants.BUSINESS_TYPE_DATE, "modifiedDate",
				dbTableName, ObjectFieldConstants.DB_TYPE_DATE, "Modified Date",
				"modifiedDate", false, false, true),
			newObjectFieldModel(
				listTypeDefinitionId, objectDefinitionId,
				ObjectFieldConstants.BUSINESS_TYPE_TEXT, "status", dbTableName,
				ObjectFieldConstants.DB_TYPE_STRING, "Status", "status", false,
				false, true));
	}

	public ObjectFieldSettingModel newObjectFieldSettingModel(
		long objectFieldId, String name, String value) {

		ObjectFieldSettingModel objectFieldSettingModel =
			new ObjectFieldSettingModelImpl();

		// PK fields

		objectFieldSettingModel.setObjectFieldSettingId(_counter.get());

		// Audit fields

		objectFieldSettingModel.setCompanyId(_companyId);
		objectFieldSettingModel.setUserId(_sampleUserId);
		objectFieldSettingModel.setUserName(_SAMPLE_USER_NAME);
		objectFieldSettingModel.setCreateDate(new Date());
		objectFieldSettingModel.setModifiedDate(new Date());

		// Other fields

		objectFieldSettingModel.setObjectFieldId(objectFieldId);
		objectFieldSettingModel.setName(name);
		objectFieldSettingModel.setValue(value);

		return objectFieldSettingModel;
	}

	public List<ObjectFieldSettingModel> newObjectFieldSettingModels(
		ObjectFieldModel objectFieldModel) {

		List<ObjectFieldSettingModel> objectFieldSettingModels =
			new ArrayList<>();

		long objectFieldId = objectFieldModel.getObjectFieldId();

		if (StringUtil.equals(
				objectFieldModel.getBusinessType(),
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

			objectFieldSettingModels.add(
				newObjectFieldSettingModel(
					objectFieldId,
					ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS,
					"jpeg, jpg, pdf, png, txt"));
			objectFieldSettingModels.add(
				newObjectFieldSettingModel(
					objectFieldId, ObjectFieldSettingConstants.NAME_FILE_SOURCE,
					ObjectFieldSettingConstants.VALUE_USER_COMPUTER));
			objectFieldSettingModels.add(
				newObjectFieldSettingModel(
					objectFieldId,
					ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE, "100"));
			objectFieldSettingModels.add(
				newObjectFieldSettingModel(
					objectFieldId,
					ObjectFieldSettingConstants.
						NAME_SHOW_FILES_IN_DOCS_AND_MEDIA,
					Boolean.TRUE.toString()));
			objectFieldSettingModels.add(
				newObjectFieldSettingModel(
					objectFieldId,
					ObjectFieldSettingConstants.NAME_STORAGE_DL_FOLDER_PATH,
					String.valueOf(objectFieldModel.getObjectDefinitionId())));
		}
		else if (StringUtil.equals(
					objectFieldModel.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST) &&
				 objectFieldModel.isState()) {

			objectFieldSettingModels.add(
				newObjectFieldSettingModel(
					objectFieldId,
					ObjectFieldSettingConstants.NAME_DEFAULT_VALUE,
					_defaultListTypeEntryKey));
			objectFieldSettingModels.add(
				newObjectFieldSettingModel(
					objectFieldId,
					ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE,
					ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE));
		}
		else if (StringUtil.equals(
					objectFieldModel.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

			objectFieldSettingModels.add(
				newObjectFieldSettingModel(
					objectFieldId, "objectRelationshipERCObjectFieldName",
					"r_userTicket_userERC"));
		}

		return objectFieldSettingModels;
	}

	public List<FragmentEntryLinkModel> newObjectFieldsFragmentEntryLinkModels(
			List<LayoutModel> layoutModels,
			List<ObjectFieldModel> objectFieldModels, long segmentsExperienceId)
		throws Exception {

		List<FragmentEntryLinkModel> nonhiddenFragmentEntryLinkModels =
			new ArrayList<>();

		String editValueJSON = _readFile(
			"fragment_component/fragment_component_heading_editValue.json");
		String headingCss = _readFile(
			_getFragmentComponentInputStream("heading", "css"));
		String headingHtml = _readFile(
			_getFragmentComponentInputStream("heading", "html"));
		String paragraphRenderNamespace = StringUtil.randomId();

		for (ObjectFieldModel objectFieldModel : objectFieldModels) {
			if (objectFieldModel.isSystem()) {
				continue;
			}

			String editValue;

			if (StringUtil.equals(
					objectFieldModel.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

				editValue = StringUtil.replace(
					_readFile(
						"fragment_component" +
							"/fragment_component_heading_editValue_" +
								"attachment_object_field.json"),
					"${objectFieldId}",
					String.valueOf(objectFieldModel.getObjectFieldId()));
			}
			else {
				editValue = StringUtil.replaceFirst(
					editValueJSON, "${collectionFieldId}",
					"ObjectField_" + objectFieldModel.getName());
			}

			nonhiddenFragmentEntryLinkModels.add(
				newFragmentEntryLinkModel(
					layoutModels.get(1), 0, segmentsExperienceId, headingCss,
					headingHtml, StringPool.BLANK, editValue,
					paragraphRenderNamespace, 0,
					_FRAGMENT_COMPONENT_RENDER_KEY_HEADING));
		}

		List<FragmentEntryLinkModel> fragmentEntryLinkModels = new ArrayList<>(
			nonhiddenFragmentEntryLinkModels);

		for (FragmentEntryLinkModel originalFragmentEntryLinkModel :
				nonhiddenFragmentEntryLinkModels) {

			fragmentEntryLinkModels.add(
				newFragmentEntryLinkModel(
					layoutModels.get(0),
					originalFragmentEntryLinkModel.getFragmentEntryLinkId(),
					originalFragmentEntryLinkModel.getSegmentsExperienceId(),
					originalFragmentEntryLinkModel.getCss(),
					originalFragmentEntryLinkModel.getHtml(),
					originalFragmentEntryLinkModel.getConfiguration(),
					originalFragmentEntryLinkModel.getEditableValues(),
					originalFragmentEntryLinkModel.getNamespace(),
					originalFragmentEntryLinkModel.getPosition(),
					originalFragmentEntryLinkModel.getRendererKey()));
		}

		return fragmentEntryLinkModels;
	}

	public ObjectFolderModel newObjectFolderModel() {
		ObjectFolderModel objectFolderModel = new ObjectFolderModelImpl();

		// PK fields

		objectFolderModel.setObjectFolderId(_counter.get());

		// Audit fields

		objectFolderModel.setCompanyId(_companyId);
		objectFolderModel.setUserId(_sampleUserId);
		objectFolderModel.setUserName(_SAMPLE_USER_NAME);
		objectFolderModel.setCreateDate(new Date());
		objectFolderModel.setModifiedDate(new Date());

		// Other field

		objectFolderModel.setLabel(
			StringBundler.concat(
				"<?xml version=\"1.0\"?><root available-locales=\"en_US\" ",
				"default-locale=\"en_US\"><Label language-id=\"en_US\">",
				"Default</Label></root>"));
		objectFolderModel.setName("Default");

		// Autogenerated fields

		objectFolderModel.setUuid(SequentialUUID.generate());
		objectFolderModel.setExternalReferenceCode("default");

		return objectFolderModel;
	}

	public ObjectRelationshipModel newObjectRelationshipModel(
		long objectDefinitionId2) {

		ObjectRelationshipModel objectRelationshipModel =
			new ObjectRelationshipModelImpl();

		// PK fields

		objectRelationshipModel.setObjectRelationshipId(_counter.get());

		// Audit fields

		objectRelationshipModel.setCompanyId(_companyId);
		objectRelationshipModel.setUserId(_sampleUserId);
		objectRelationshipModel.setUserName(_SAMPLE_USER_NAME);
		objectRelationshipModel.setCreateDate(new Date());
		objectRelationshipModel.setModifiedDate(new Date());

		// Other fields

		objectRelationshipModel.setObjectDefinitionId1(_objectDefinitionId);
		objectRelationshipModel.setObjectDefinitionId2(objectDefinitionId2);
		objectRelationshipModel.setObjectFieldId2(_objectFieldId);
		objectRelationshipModel.setParameterObjectFieldId(0);
		objectRelationshipModel.setDeletionType(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE);
		objectRelationshipModel.setEdge(false);

		String name =
			"ObjectRelationship" + _objectDefinitionId + objectDefinitionId2;

		objectRelationshipModel.setLabel(_getObjectLabel(name));
		objectRelationshipModel.setName(name);

		objectRelationshipModel.setReverse(false);
		objectRelationshipModel.setSystem(false);
		objectRelationshipModel.setType(
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		objectRelationshipModel.setExternalReferenceCode(uuid);
		objectRelationshipModel.setUuid(uuid);

		return objectRelationshipModel;
	}

	public ObjectStateFlowModel newObjectStateFlowModel(long objectFieldId) {
		ObjectStateFlowModel objectStateFlowModel =
			new ObjectStateFlowModelImpl();

		// PK fields

		objectStateFlowModel.setObjectStateFlowId(_counter.get());

		// Audit fields

		objectStateFlowModel.setCompanyId(_companyId);
		objectStateFlowModel.setUserId(_sampleUserId);
		objectStateFlowModel.setUserName(_SAMPLE_USER_NAME);
		objectStateFlowModel.setCreateDate(new Date());
		objectStateFlowModel.setModifiedDate(new Date());

		// Other fields

		objectStateFlowModel.setObjectFieldId(objectFieldId);

		return objectStateFlowModel;
	}

	public List<ObjectStateModel> newObjectStateModels(
		List<ListTypeEntryModel> listTypeEntryModels, long objectStateFlowId) {

		List<ObjectStateModel> objectStateModels = new ArrayList<>(
			listTypeEntryModels.size());

		for (ListTypeEntryModel listTypeEntryModel : listTypeEntryModels) {
			objectStateModels.add(
				newObjectStateModel(
					listTypeEntryModel.getListTypeEntryId(),
					objectStateFlowId));
		}

		return objectStateModels;
	}

	public List<ObjectStateTransitionModel> newObjectStateTransitionModels(
		List<ObjectStateModel> objectStateModels) {

		List<ObjectStateTransitionModel> objectStateTransitionModels =
			new ArrayList<>();

		for (ObjectStateModel sourceObjectState : objectStateModels) {
			for (ObjectStateModel targetObjectState : objectStateModels) {
				if (sourceObjectState.equals(targetObjectState)) {
					continue;
				}

				objectStateTransitionModels.add(
					newObjectStateTransitionModel(
						sourceObjectState.getObjectStateFlowId(),
						sourceObjectState.getObjectStateId(),
						targetObjectState.getObjectStateId()));
			}
		}

		return objectStateTransitionModels;
	}

	public <K, V> ObjectValuePair<K, V> newObjectValuePair(K key, V value) {
		return new ObjectValuePair<>(key, value);
	}

	public PortalPreferencesModel newPortalPreferencesModel(long ownerId) {
		PortalPreferencesModel portalPreferencesModel =
			new PortalPreferencesModelImpl();

		// PK fields

		portalPreferencesModel.setPortalPreferencesId(_counter.get());

		// Audit fields

		portalPreferencesModel.setCompanyId(_companyId);

		// Other fields

		portalPreferencesModel.setOwnerId(ownerId);
		portalPreferencesModel.setOwnerType(
			PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		return portalPreferencesModel;
	}

	public PortletPreferencesModel newPortletPreferencesModel(
		long ownerId, int ownerType, long plid, String portletId) {

		PortletPreferencesModel portletPreferencesModel =
			new PortletPreferencesModelImpl();

		// PK fields

		portletPreferencesModel.setPortletPreferencesId(_counter.get());

		// Audit fields

		portletPreferencesModel.setCompanyId(_companyId);

		// Other fields

		portletPreferencesModel.setOwnerId(ownerId);
		portletPreferencesModel.setOwnerType(ownerType);
		portletPreferencesModel.setPlid(plid);
		portletPreferencesModel.setPortletId(portletId);

		return portletPreferencesModel;
	}

	public PortletPreferencesModel newPortletPreferencesModel(
		long plid, long groupId, String portletId, int currentIndex) {

		if (currentIndex == 1) {
			return newPortletPreferencesModel(plid, portletId);
		}

		String assetPublisherQueryName = "assetCategories";

		if ((currentIndex % 2) == 0) {
			assetPublisherQueryName = "assetTags";
		}

		ObjectValuePair<String[], Integer> objectValuePair = null;

		Integer startIndex = _assetPublisherQueryStartIndexes.get(groupId);

		if (startIndex == null) {
			startIndex = 0;
		}

		if (assetPublisherQueryName.equals("assetCategories")) {
			Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap =
				_assetCategoryModelsMaps[(int)groupId - 1];

			List<AssetCategoryModel> assetCategoryModels =
				assetCategoryModelsMap.get(getNextAssetClassNameId(groupId));

			if (ListUtil.isEmpty(assetCategoryModels)) {
				return newPortletPreferencesModel(plid, portletId);
			}

			objectValuePair = getAssetPublisherAssetCategoriesQueryValues(
				assetCategoryModels, startIndex);
		}
		else {
			Map<Long, List<AssetTagModel>> assetTagModelsMap =
				_assetTagModelsMaps[(int)groupId - 1];

			List<AssetTagModel> assetTagModels = assetTagModelsMap.get(
				getNextAssetClassNameId(groupId));

			if (ListUtil.isEmpty(assetTagModels)) {
				return newPortletPreferencesModel(plid, portletId);
			}

			objectValuePair = getAssetPublisherAssetTagsQueryValues(
				assetTagModels, startIndex);
		}

		_assetPublisherQueryStartIndexes.put(
			groupId, objectValuePair.getValue());

		return newPortletPreferencesModel(plid, portletId);
	}

	public PortletPreferencesModel newPortletPreferencesModel(
		long plid, String portletId) {

		return newPortletPreferencesModel(
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, plid, portletId);
	}

	public List<ReleaseModel> newReleaseModels() throws Exception {
		List<ReleaseModel> releases = new ArrayList<>();

		releases.add(
			newReleaseModel(
				ReleaseConstants.DEFAULT_ID,
				ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME,
				String.valueOf(PortalUpgradeProcess.getLatestSchemaVersion()),
				ReleaseInfo.getBuildNumber(), false,
				ReleaseConstants.TEST_STRING));

		for (String release :
				_readLines(
					DataFactory.class.getResourceAsStream(
						"dependencies/releases.txt"))) {

			String[] parts = StringUtil.split(release, CharPool.COLON);

			if (parts.length > 0) {
				String servletContextName = parts[0];
				String schemaVersion = parts[1];

				releases.add(
					newReleaseModel(
						_counter.get(), servletContextName, schemaVersion, 0,
						true, null));
			}
		}

		return releases;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AccountEntryModel accountEntryModel) {

		return newResourcePermissionModels(
			AccountEntry.class.getName(),
			String.valueOf(accountEntryModel.getAccountEntryId()),
			_sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AddressModel addressModel) {

		return newResourcePermissionModels(
			Address.class.getName(),
			String.valueOf(addressModel.getAddressId()), _sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetCategoryModel assetCategoryModel) {

		return newResourcePermissionModels(
			AssetCategory.class.getName(),
			String.valueOf(assetCategoryModel.getCategoryId()), _sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetListEntryModel assetListEntryModel) {

		return newResourcePermissionModels(
			AssetListEntry.class.getName(),
			String.valueOf(assetListEntryModel.getAssetListEntryId()),
			_sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetVocabularyModel assetVocabularyModel) {

		if (assetVocabularyModel.getUserId() == _guestUserId) {
			return Collections.singletonList(
				newResourcePermissionModel(
					AssetVocabulary.class.getName(),
					String.valueOf(assetVocabularyModel.getVocabularyId()),
					_ownerRoleModel.getRoleId(), _guestUserId));
		}

		return newResourcePermissionModels(
			AssetVocabulary.class.getName(),
			String.valueOf(assetVocabularyModel.getVocabularyId()),
			_sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		BlogsEntryModel blogsEntryModel) {

		return newResourcePermissionModels(
			BlogsEntry.class.getName(),
			String.valueOf(blogsEntryModel.getEntryId()), _sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		CommerceInventoryWarehouseModel commerceInventoryWarehouseModel) {

		return newResourcePermissionModels(
			CommerceInventoryWarehouse.class.getName(),
			String.valueOf(
				commerceInventoryWarehouseModel.
					getCommerceInventoryWarehouseId()),
			_sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		CommercePriceListModel commercePriceListModel) {

		return newResourcePermissionModels(
			CommercePriceList.class.getName(),
			String.valueOf(commercePriceListModel.getCommercePriceListId()),
			_sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		CommerceShippingFixedOptionModel commerceShippingFixedOptionModel) {

		return newResourcePermissionModels(
			CommerceShippingFixedOption.class.getName(),
			String.valueOf(
				commerceShippingFixedOptionModel.
					getCommerceShippingFixedOptionId()),
			_sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		CommerceShippingMethodModel commerceShippingMethodModel) {

		return newResourcePermissionModels(
			CommerceShippingMethod.class.getName(),
			String.valueOf(
				commerceShippingMethodModel.getCommerceShippingMethodId()),
			_sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		CountryModel countryModel) {

		return newResourcePermissionModels(
			Country.class.getName(),
			String.valueOf(countryModel.getCountryId()), _sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		CPOptionCategoryModel cpOptionCategoryModel) {

		return newResourcePermissionModels(
			CPOptionCategory.class.getName(),
			String.valueOf(cpOptionCategoryModel.getCPOptionCategoryId()),
			_sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		CPOptionModel cpOptionModel) {

		return newResourcePermissionModels(
			CPOption.class.getName(),
			String.valueOf(cpOptionModel.getCPOptionId()), _sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		CPSpecificationOptionModel cpSpecificationOptionModel) {

		return newResourcePermissionModels(
			CPSpecificationOption.class.getName(),
			String.valueOf(
				cpSpecificationOptionModel.getCPSpecificationOptionId()),
			_sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDLRecordSetModel ddlRecordSetModel) {

		return newResourcePermissionModels(
			DDLRecordSet.class.getName(),
			String.valueOf(ddlRecordSetModel.getRecordSetId()), _sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMStructureModel ddmStructureModel) {

		String name = _getResourcePermissionModelName(
			DDMStructure.class.getName(),
			getClassName(ddmStructureModel.getClassNameId()));
		String primKey = String.valueOf(ddmStructureModel.getStructureId());

		return newResourcePermissionModels(name, primKey, _sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMTemplateModel ddmTemplateModel) {

		String className = getClassName(
			ddmTemplateModel.getResourceClassNameId());

		String name = _getResourcePermissionModelName(
			DDMTemplate.class.getName(), className);

		if (className.equals(PortletDisplayTemplate.class.getName())) {
			name = DDMTemplate.class.getName();
		}

		String primKey = String.valueOf(ddmTemplateModel.getTemplateId());

		return newResourcePermissionModels(name, primKey, _sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFileEntryModel dlFileEntryModel) {

		return newResourcePermissionModels(
			DLFileEntry.class.getName(),
			String.valueOf(dlFileEntryModel.getFileEntryId()), _sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFolderModel dlFolderModel) {

		return newResourcePermissionModels(
			DLFolder.class.getName(),
			String.valueOf(dlFolderModel.getFolderId()), _sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		GroupModel groupModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				Group.class.getName(), String.valueOf(groupModel.getGroupId()),
				_ownerRoleModel.getRoleId(), _sampleUserId));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		JournalArticleResourceModel journalArticleResourceModel) {

		return newResourcePermissionModels(
			JournalArticle.class.getName(),
			String.valueOf(journalArticleResourceModel.getResourcePrimKey()),
			_sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		LayoutModel layoutModel) {

		return newResourcePermissionModels(
			Layout.class.getName(), String.valueOf(layoutModel.getPlid()), 0);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		ListTypeDefinitionModel listTypeDefinitionModel) {

		return newResourcePermissionModels(
			ListTypeDefinition.class.getName(),
			String.valueOf(listTypeDefinitionModel.getListTypeDefinitionId()),
			_sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		MBCategoryModel mbCategoryModel) {

		return newResourcePermissionModels(
			MBCategory.class.getName(),
			String.valueOf(mbCategoryModel.getCategoryId()), _sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		MBMessageModel mbMessageModel) {

		return newResourcePermissionModels(
			MBMessage.class.getName(),
			String.valueOf(mbMessageModel.getMessageId()), _sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		ObjectDefinitionModel objectDefinitionModel) {

		return newResourcePermissionModels(
			ObjectDefinition.class.getName(),
			String.valueOf(objectDefinitionModel.getObjectDefinitionId()),
			_sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		ObjectEntryModel objectEntryModel) {

		return newResourcePermissionModels(
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
					objectEntryModel.getObjectDefinitionId(),
			String.valueOf(objectEntryModel.getObjectEntryId()), _sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		ObjectFolderModel objectFolderModel) {

		return newResourcePermissionModels(
			ObjectFolder.class.getName(),
			String.valueOf(objectFolderModel.getObjectFolderId()),
			_sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		PortletPreferencesModel portletPreferencesModel) {

		String portletId = portletPreferencesModel.getPortletId();

		String name = portletId;

		String primKey = PortletPermissionUtil.getPrimaryKey(
			portletPreferencesModel.getPlid(), portletId);

		if (portletPreferencesModel.getPlid() <= 0) {
			primKey = String.valueOf(portletPreferencesModel.getOwnerId());
		}

		return newResourcePermissionModels(name, primKey, 0);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		RoleModel roleModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				Role.class.getName(), String.valueOf(roleModel.getRoleId()),
				_ownerRoleModel.getRoleId(), _sampleUserId));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		SegmentsEntryModel segmentsEntryModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				SegmentsEntry.class.getName(),
				String.valueOf(segmentsEntryModel.getSegmentsEntryId()),
				_guestRoleModel.getRoleId(), _sampleUserId));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		String name, long primKey) {

		return newResourcePermissionModels(
			name, String.valueOf(primKey), _sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		UserModel userModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				User.class.getName(), String.valueOf(userModel.getUserId()),
				_ownerRoleModel.getRoleId(), userModel.getUserId()));
	}

	public List<RoleModel> newRoleModels() {
		List<RoleModel> roleModels = new ArrayList<>();

		// Administrator

		_administratorRoleModel = newRoleModel(
			RoleConstants.ADMINISTRATOR, RoleConstants.TYPE_REGULAR);

		roleModels.add(_administratorRoleModel);

		// Guest

		_guestRoleModel = newRoleModel(
			RoleConstants.GUEST, RoleConstants.TYPE_REGULAR);

		roleModels.add(_guestRoleModel);

		// Organization Administrator

		roleModels.add(
			newRoleModel(
				RoleConstants.ORGANIZATION_ADMINISTRATOR,
				RoleConstants.TYPE_ORGANIZATION));

		// Organization Owner

		roleModels.add(
			newRoleModel(
				RoleConstants.ORGANIZATION_OWNER,
				RoleConstants.TYPE_ORGANIZATION));

		// Organization User

		roleModels.add(
			newRoleModel(
				RoleConstants.ORGANIZATION_USER,
				RoleConstants.TYPE_ORGANIZATION));

		// Owner

		_ownerRoleModel = newRoleModel(
			RoleConstants.OWNER, RoleConstants.TYPE_REGULAR);

		roleModels.add(_ownerRoleModel);

		// Power User

		_powerUserRoleModel = newRoleModel(
			RoleConstants.POWER_USER, RoleConstants.TYPE_REGULAR);

		roleModels.add(_powerUserRoleModel);

		// Site Administrator

		roleModels.add(
			newRoleModel(
				RoleConstants.SITE_ADMINISTRATOR, RoleConstants.TYPE_SITE));

		// Site Member

		_siteMemberRoleModel = newRoleModel(
			RoleConstants.SITE_MEMBER, RoleConstants.TYPE_SITE);

		roleModels.add(_siteMemberRoleModel);

		// Site Owner

		roleModels.add(
			newRoleModel(RoleConstants.SITE_OWNER, RoleConstants.TYPE_SITE));

		// User

		_userRoleModel = newRoleModel(
			RoleConstants.USER, RoleConstants.TYPE_REGULAR);

		roleModels.add(_userRoleModel);

		return roleModels;
	}

	public UserModel newSampleUserModel() {
		_sampleUserId = _counter.get();

		return newUserModel(
			_sampleUserId, _SAMPLE_USER_NAME, _SAMPLE_USER_NAME,
			_SAMPLE_USER_NAME, UserConstants.TYPE_REGULAR);
	}

	public LayoutModel newSearchGroupLayoutModel(
		long groupId, LayoutModel layoutModel) {

		return newLayoutModel(
			"layout", groupId, false, layoutModel.getName(),
			layoutModel.isPrivateLayout(), layoutModel.getParentLayoutId(),
			layoutModel.getTypeSettings());
	}

	public LayoutModel newSearchLayoutModel(long groupId, boolean hidden) {
		return newLayoutModel(
			groupId, hidden, "1_2_columns_i", "search", false, 0,
			new String[] {
				StringBundler.concat(
					SearchBarPortletKeys.SEARCH_BAR, StringPool.COMMA,
					SuggestionsPortletKeys.SUGGESTIONS, StringPool.COMMA),
				StringBundler.concat(
					SiteFacetPortletKeys.SITE_FACET, StringPool.COMMA,
					TypeFacetPortletKeys.TYPE_FACET, StringPool.COMMA,
					TagFacetPortletKeys.TAG_FACET, StringPool.COMMA,
					CategoryFacetPortletKeys.CATEGORY_FACET, StringPool.COMMA,
					FolderFacetPortletKeys.FOLDER_FACET, StringPool.COMMA,
					UserFacetPortletKeys.USER_FACET, StringPool.COMMA,
					ModifiedFacetPortletKeys.MODIFIED_FACET, StringPool.COMMA),
				StringBundler.concat(
					SearchResultsPortletKeys.SEARCH_RESULTS, StringPool.COMMA,
					SearchOptionsPortletKeys.SEARCH_OPTIONS, StringPool.COMMA)
			});
	}

	public GroupModel newSearchTemplateGroupModel(
		long layoutPrototypeId, long userId) {

		return newGroupModel(
			getClassNameId(LayoutPrototype.class), layoutPrototypeId,
			"template-" + String.valueOf(layoutPrototypeId), _counter.get(),
			String.valueOf(layoutPrototypeId), "Search", false, 0,
			StringPool.BLANK, userId);
	}

	public SegmentsEntryModel newSegmentsEntryModel(long groupId, int index) {
		SegmentsEntryModel segmentsEntryModel = new SegmentsEntryModelImpl();

		// PK fields

		segmentsEntryModel.setSegmentsEntryId(_counter.get());

		// Group instance

		segmentsEntryModel.setGroupId(groupId);

		// Audit fields

		segmentsEntryModel.setCompanyId(_companyId);
		segmentsEntryModel.setUserId(_sampleUserId);
		segmentsEntryModel.setUserName(_SAMPLE_USER_NAME);
		segmentsEntryModel.setCreateDate(new Date());
		segmentsEntryModel.setModifiedDate(new Date());

		// Other fields

		segmentsEntryModel.setSegmentsEntryKey(_counter.getString());
		segmentsEntryModel.setName(
			StringBundler.concat(
				"<?xml version=\"1.0\"?><root available-locales=\"en_US\" ",
				"default-locale=\"en_US\"><Name language-id=\"en_US\">",
				"SampleSegment", index, "</Name></root>"));
		segmentsEntryModel.setActive(true);

		Criteria criteria = new Criteria();

		String filterString = StringBundler.concat(
			"(firstName eq ''", _SAMPLE_USER_NAME, index, "'')");

		criteria.addCriterion(
			"user", Criteria.Type.MODEL, filterString,
			Criteria.Conjunction.AND);

		criteria.addFilter(
			Criteria.Type.MODEL, filterString, Criteria.Conjunction.AND);

		segmentsEntryModel.setCriteria(CriteriaSerializer.serialize(criteria));

		segmentsEntryModel.setSource(SegmentsEntryConstants.SOURCE_DEFAULT);

		// Autogenerated fields

		segmentsEntryModel.setUuid(SequentialUUID.generate());

		return segmentsEntryModel;
	}

	public List<SegmentsEntryModel> newSegmentsEntryModels(long groupId) {
		List<SegmentsEntryModel> segmentsEntryModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_SEGMENTS_ENTRY_COUNT);

		for (int i = 0; i < BenchmarksPropsValues.MAX_SEGMENTS_ENTRY_COUNT;
			 i++) {

			segmentsEntryModels.add(newSegmentsEntryModel(groupId, i));
		}

		return segmentsEntryModels;
	}

	public SegmentsExperienceModel newSegmentsExperienceModel(
		List<LayoutModel> layoutModels) {

		long groupId = 0;
		long plid = 0;

		for (LayoutModel layoutModel : layoutModels) {
			long classNameId = layoutModel.getClassNameId();

			if (classNameId == 0) {
				groupId = layoutModel.getGroupId();
				plid = layoutModel.getPlid();

				break;
			}
		}

		return newSegmentsExperienceModel(
			groupId, 0, "DEFAULT", plid, "Default", 0);
	}

	public SegmentsExperienceModel newSegmentsExperienceModel(
		long groupId, long segmentsEntryId, long plid) {

		Long index = _segmentsExperienceCounter.get();

		return newSegmentsExperienceModel(
			groupId, segmentsEntryId, _counter.getString(), plid,
			"SampleExperience" + index, index.intValue());
	}

	public SocialActivityModel newSocialActivityModel(
		BlogsEntryModel blogsEntryModel) {

		return newSocialActivityModel(
			blogsEntryModel.getGroupId(), getClassNameId(BlogsEntry.class),
			blogsEntryModel.getEntryId(), BlogsActivityKeys.ADD_ENTRY,
			"{\"title\":\"" + blogsEntryModel.getTitle() + "\"}");
	}

	public SocialActivityModel newSocialActivityModel(
		DLFileEntryModel dlFileEntryModel) {

		return newSocialActivityModel(
			dlFileEntryModel.getGroupId(), getClassNameId(DLFileEntry.class),
			dlFileEntryModel.getFileEntryId(), DLActivityKeys.ADD_FILE_ENTRY,
			StringPool.BLANK);
	}

	public SocialActivityModel newSocialActivityModel(
		JournalArticleModel journalArticleModel) {

		int type = JournalActivityKeys.UPDATE_ARTICLE;

		if (journalArticleModel.getVersion() ==
				JournalArticleConstants.VERSION_DEFAULT) {

			type = JournalActivityKeys.ADD_ARTICLE;
		}

		return newSocialActivityModel(
			journalArticleModel.getGroupId(),
			getClassNameId(JournalArticle.class),
			journalArticleModel.getResourcePrimKey(), type,
			"{\"title\":\"" + journalArticleModel.getUrlTitle() + "\"}");
	}

	public SocialActivityModel newSocialActivityModel(
		MBMessageModel mbMessageModel) {

		long classNameId = mbMessageModel.getClassNameId();
		long classPK = mbMessageModel.getClassPK();

		int type = 0;
		String extraData = null;

		if (classNameId == 0) {
			extraData = "{\"title\":\"" + mbMessageModel.getSubject() + "\"}";

			type = MBActivityKeys.ADD_MESSAGE;

			classNameId = getClassNameId(MBMessage.class);
			classPK = mbMessageModel.getMessageId();
		}
		else {
			extraData = StringBundler.concat(
				"{\"messageId\": \"", mbMessageModel.getMessageId(),
				"\", \"title\": ", mbMessageModel.getSubject(), "}");

			type = SocialActivityConstants.TYPE_ADD_COMMENT;
		}

		return newSocialActivityModel(
			mbMessageModel.getGroupId(), classNameId, classPK, type, extraData);
	}

	public SubscriptionModel newSubscriptionModel(
		BlogsEntryModel blogsEntryModel) {

		return newSubscriptionModel(
			getClassNameId(BlogsEntry.class), blogsEntryModel.getEntryId());
	}

	public SubscriptionModel newSubscriptionModel(MBThreadModel mBThreadModel) {
		return newSubscriptionModel(
			getClassNameId(MBThread.class), mBThreadModel.getThreadId());
	}

	public List<UserModel> newUserModels() {
		List<UserModel> userModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_COMPANY_USER_COUNT);

		for (int i = 0; i < BenchmarksPropsValues.MAX_COMPANY_USER_COUNT; i++) {
			String[] userName = nextUserName(i);

			userModels.add(
				newUserModel(
					_counter.get(), userName[0], userName[1],
					"test" + _userScreenNameCounter.get(),
					UserConstants.TYPE_REGULAR));
		}

		return userModels;
	}

	public GroupModel newUserPersonalSiteGroupModel() {
		return newGroupModel(
			getClassNameId(UserPersonalSite.class), _guestUserId,
			_counter.get(), GroupConstants.USER_PERSONAL_SITE, false);
	}

	public VirtualHostModel newVirtualHostModel() {
		VirtualHostModel virtualHostModel = new VirtualHostModelImpl();

		//  PK fields

		virtualHostModel.setVirtualHostId(_counter.get());

		// Audit fields

		virtualHostModel.setCompanyId(_companyId);

		// Other fields

		if (_webId.equals("liferay.com")) {
			virtualHostModel.setHostname(
				BenchmarksPropsValues.VIRTUAL_HOSTNAME_ADMIN_INSTANCE);
		}
		else {
			virtualHostModel.setHostname(_webId);
		}

		virtualHostModel.setDefaultVirtualHost(true);

		return virtualHostModel;
	}

	public String[] nextUserName(long index) {
		String[] userName = new String[2];

		userName[0] = _firstNames.get(
			(int)(index / _lastNames.size()) % _firstNames.size());
		userName[1] = _lastNames.get((int)(index % _lastNames.size()));

		return userName;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public void setWebId(String webId) {
		_webId = webId;
	}

	public String toInsertSQL(BaseModel<?> baseModel) {
		try {
			StringBundler sb = new StringBundler();

			toInsertSQL(sb, baseModel);

			Class<?> clazz = baseModel.getClass();

			for (Class<?> modelClass : clazz.getInterfaces()) {
				try {
					Method method = DataFactory.class.getMethod(
						"newResourcePermissionModels", modelClass);

					for (ResourcePermissionModel resourcePermissionModel :
							(List<ResourcePermissionModel>)method.invoke(
								this, baseModel)) {

						sb.append("\n");

						toInsertSQL(sb, resourcePermissionModel);
					}
				}
				catch (NoSuchMethodException noSuchMethodException) {
					if (_log.isDebugEnabled()) {
						_log.debug(noSuchMethodException);
					}
				}
			}

			return sb.toString();
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			return ReflectionUtil.throwException(reflectiveOperationException);
		}
	}

	public String toInsertSQL(
		String mappingTableName, long companyId, long leftPrimaryKey,
		long rightPrimaryKey) {

		return StringBundler.concat(
			"insert into ", mappingTableName, " values (", companyId, ", ",
			leftPrimaryKey, ", ", rightPrimaryKey, ", 0, null);");
	}

	protected ObjectValuePair<String[], Integer>
		getAssetPublisherAssetCategoriesQueryValues(
			List<AssetCategoryModel> assetCategoryModels, int index) {

		String[] categoryIds = new String[4];

		for (int i = 0; i < 4; i++) {
			if (i > 0) {
				index +=
					BenchmarksPropsValues.
						MAX_ASSET_ENTRY_TO_ASSET_CATEGORY_COUNT;
			}

			AssetCategoryModel assetCategoryModel = assetCategoryModels.get(
				index % assetCategoryModels.size());

			categoryIds[i] = String.valueOf(assetCategoryModel.getCategoryId());
		}

		return new ObjectValuePair<>(
			categoryIds,
			index +
				BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_CATEGORY_COUNT);
	}

	protected ObjectValuePair<String[], Integer>
		getAssetPublisherAssetTagsQueryValues(
			List<AssetTagModel> assetTagModels, int index) {

		String[] assetTagNames = new String[4];

		for (int i = 0; i < 4; i++) {
			if (i > 0) {
				index +=
					BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_TAG_COUNT;
			}

			AssetTagModel assetTagModel = assetTagModels.get(
				index % assetTagModels.size());

			assetTagNames[i] = String.valueOf(assetTagModel.getName());
		}

		return new ObjectValuePair<>(
			assetTagNames,
			index + BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_TAG_COUNT);
	}

	protected String getClassName(long classNameId) {
		for (ClassNameModel classNameModel : _classNameModels.values()) {
			if (classNameModel.getClassNameId() == classNameId) {
				return classNameModel.getValue();
			}
		}

		throw new RuntimeException(
			"Unable to find class name for id " + classNameId);
	}

	protected String[] getPortletNames(JSONArray jsonArray) {
		Map<String, String> portletNames = new LinkedHashMap<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String portletName = jsonObject.getString("portletName");

			if (ArrayUtil.contains(
					BenchmarksPropsValues.COMMERCE_LAYOUT_EXCLUDED_PORTLETS,
					portletName)) {

				continue;
			}

			String key = jsonObject.getString("layoutColumnId");

			if (portletNames.containsKey(key)) {
				portletNames.put(
					key,
					portletNames.get(key) + StringPool.COMMA + portletName);
			}
			else {
				portletNames.put(key, portletName);
			}
		}

		return ArrayUtil.toStringArray(portletNames.values());
	}

	protected InputStream getResourceInputStream(String resourceName) {
		return DataFactory.class.getResourceAsStream(
			_DEPENDENCIES_DIR + resourceName);
	}

	protected SimpleCounter getSimpleCounter(
		Map<Long, SimpleCounter>[] simpleCountersArray, long groupId,
		long classNameId) {

		Map<Long, SimpleCounter> simpleCounters =
			simpleCountersArray[(int)groupId - 1];

		if (simpleCounters == null) {
			simpleCounters = new HashMap<>();

			simpleCountersArray[(int)groupId - 1] = simpleCounters;
		}

		SimpleCounter simpleCounter = simpleCounters.get(classNameId);

		if (simpleCounter == null) {
			simpleCounter = new SimpleCounter(0);

			simpleCounters.put(classNameId, simpleCounter);
		}

		return simpleCounter;
	}

	protected AssetCategoryModel newAssetCategoryModel(
		long groupId, String name, long vocabularyId) {

		AssetCategoryModel assetCategoryModel = new AssetCategoryModelImpl();

		// PK fields

		assetCategoryModel.setCategoryId(_counter.get());

		// Group instance

		assetCategoryModel.setGroupId(groupId);

		// Audit fields

		assetCategoryModel.setCompanyId(_companyId);
		assetCategoryModel.setUserId(_sampleUserId);
		assetCategoryModel.setUserName(_SAMPLE_USER_NAME);
		assetCategoryModel.setCreateDate(new Date());
		assetCategoryModel.setModifiedDate(new Date());

		// Other fields

		assetCategoryModel.setParentCategoryId(
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
		assetCategoryModel.setTreePath(
			"/" + assetCategoryModel.getCategoryId() + "/");
		assetCategoryModel.setName(name);
		assetCategoryModel.setTitle(
			StringBundler.concat(
				"<?xml version=\"1.0\"?><root available-locales=\"en_US\" ",
				"default-locale=\"en_US\"><Title language-id=\"en_US\">", name,
				"</Title></root>"));
		assetCategoryModel.setVocabularyId(vocabularyId);
		assetCategoryModel.setLastPublishDate(new Date());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		assetCategoryModel.setUuid(uuid);
		assetCategoryModel.setExternalReferenceCode(uuid);

		return assetCategoryModel;
	}

	protected AssetEntryModel newAssetEntryModel(
		long groupId, Date createDate, Date modifiedDate, long classNameId,
		long classPK, String uuid, long classTypeId, boolean listable,
		boolean visible, String mimeType, String title) {

		AssetEntryModel assetEntryModel = new AssetEntryModelImpl();

		// PK fields

		assetEntryModel.setEntryId(_counter.get());

		// Group instance

		assetEntryModel.setGroupId(groupId);

		// Audit fields

		assetEntryModel.setCompanyId(_companyId);
		assetEntryModel.setUserId(_sampleUserId);
		assetEntryModel.setUserName(_SAMPLE_USER_NAME);
		assetEntryModel.setCreateDate(createDate);
		assetEntryModel.setModifiedDate(modifiedDate);

		// Other fields

		assetEntryModel.setClassNameId(classNameId);
		assetEntryModel.setClassPK(classPK);
		assetEntryModel.setClassUuid(uuid);
		assetEntryModel.setClassTypeId(classTypeId);
		assetEntryModel.setListable(listable);
		assetEntryModel.setVisible(visible);
		assetEntryModel.setStartDate(createDate);
		assetEntryModel.setEndDate(nextFutureDate());
		assetEntryModel.setPublishDate(createDate);
		assetEntryModel.setExpirationDate(nextFutureDate());
		assetEntryModel.setMimeType(mimeType);
		assetEntryModel.setTitle(title);

		return assetEntryModel;
	}

	protected AssetVocabularyModel newAssetVocabularyModel(
		long grouId, long userId, String userName, String name) {

		AssetVocabularyModel assetVocabularyModel =
			new AssetVocabularyModelImpl();

		// PK fields

		assetVocabularyModel.setVocabularyId(_counter.get());

		// Group instance

		assetVocabularyModel.setGroupId(grouId);

		// Audit fields

		assetVocabularyModel.setCompanyId(_companyId);
		assetVocabularyModel.setUserId(userId);
		assetVocabularyModel.setUserName(userName);
		assetVocabularyModel.setCreateDate(new Date());
		assetVocabularyModel.setModifiedDate(new Date());

		// Other fields

		assetVocabularyModel.setName(name);
		assetVocabularyModel.setTitle(
			StringBundler.concat(
				"<?xml version=\"1.0\"?><root available-locales=\"en_US\" ",
				"default-locale=\"en_US\"><Title language-id=\"en_US\">", name,
				"</Title></root>"));
		assetVocabularyModel.setSettings(
			"multiValued=true\\nselectedClassNameIds=0");
		assetVocabularyModel.setLastPublishDate(new Date());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		assetVocabularyModel.setUuid(uuid);
		assetVocabularyModel.setExternalReferenceCode(uuid);

		return assetVocabularyModel;
	}

	protected BlogsEntryModel newBlogsEntryModel(long groupId, int index) {
		BlogsEntryModel blogsEntryModel = new BlogsEntryModelImpl();

		// PK fields

		blogsEntryModel.setEntryId(_counter.get());

		// Group instance

		blogsEntryModel.setGroupId(groupId);

		// Audit fields

		blogsEntryModel.setCompanyId(_companyId);
		blogsEntryModel.setUserId(_sampleUserId);
		blogsEntryModel.setUserName(_SAMPLE_USER_NAME);
		blogsEntryModel.setCreateDate(new Date());
		blogsEntryModel.setModifiedDate(new Date());

		// Other field

		blogsEntryModel.setTitle("Test Blog " + index);
		blogsEntryModel.setSubtitle("Subtitle of Test Blog " + index);
		blogsEntryModel.setUrlTitle("testblog" + index);
		blogsEntryModel.setContent("This is test blog " + index + ".");
		blogsEntryModel.setDisplayDate(new Date());
		blogsEntryModel.setLastPublishDate(new Date());
		blogsEntryModel.setStatusByUserId(_sampleUserId);
		blogsEntryModel.setStatusDate(new Date());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		blogsEntryModel.setUuid(uuid);
		blogsEntryModel.setExternalReferenceCode(uuid);

		return blogsEntryModel;
	}

	protected List<PortletPreferenceValueModel>
			newCommercePortletPreferenceValueModels(
				List<PortletPreferencesModel> portletPreferencesModels,
				JSONArray jsonArray)
		throws Exception {

		List<PortletPreferenceValueModel> portletPreferenceValueModels =
			new ArrayList<>();

		for (PortletPreferencesModel portletPreferencesModel :
				portletPreferencesModels) {

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);

				String portletId = jsonObject.getString("portletName");

				if (jsonObject.getString("instanceId") != null) {
					portletId =
						portletId + "_INSTANCE_" +
							jsonObject.getString("instanceId");
				}

				if (portletId.equals(portletPreferencesModel.getPortletId())) {
					JSONObject portletPreferencesJSONObject =
						jsonObject.getJSONObject("portletPreferences");

					if (portletPreferencesJSONObject == null) {
						continue;
					}

					for (String key : portletPreferencesJSONObject.keySet()) {
						String value = portletPreferencesJSONObject.getString(
							key);

						if (key.equals("displayStyle")) {
							JSONObject displayStyleJSONObject =
								portletPreferencesJSONObject.getJSONObject(key);

							String name = StringUtil.removeSubstring(
								displayStyleJSONObject.getString("FileName"),
								".ftl");

							value = "ddmTemplate_" + name;
						}

						portletPreferenceValueModels.add(
							newPortletPreferenceValueModel(
								portletPreferencesModel, key, 0, value));
					}

					break;
				}
			}
		}

		return portletPreferenceValueModels;
	}

	protected DDMFieldAttributeModel newDDMFieldAttributeModel(
		DDMFieldModel ddmFieldModel, long storageId, String attributeName,
		String languageId, String smallAttributeValue) {

		return newDDMFieldAttributeModel(
			ddmFieldModel, storageId, attributeName, languageId,
			smallAttributeValue, "");
	}

	protected DDMFieldAttributeModel newDDMFieldAttributeModel(
		DDMFieldModel ddmFieldModel, long storageId, String attributeName,
		String languageId, String smallAttributeValue,
		String largeAttributeValue) {

		DDMFieldAttributeModel ddmFieldAttributeModel =
			new DDMFieldAttributeModelImpl();

		//  PK fields

		ddmFieldAttributeModel.setFieldAttributeId(_counter.get());

		// Audit fields

		ddmFieldAttributeModel.setCompanyId(_companyId);

		// Other fields

		ddmFieldAttributeModel.setFieldId(ddmFieldModel.getFieldId());
		ddmFieldAttributeModel.setStorageId(storageId);
		ddmFieldAttributeModel.setAttributeName(attributeName);
		ddmFieldAttributeModel.setLanguageId(languageId);
		ddmFieldAttributeModel.setLargeAttributeValue(largeAttributeValue);
		ddmFieldAttributeModel.setSmallAttributeValue(smallAttributeValue);

		return ddmFieldAttributeModel;
	}

	protected DDMFieldModel newDDMFieldModel(
		long storageId, long structureVersionId, String fieldName,
		String fieldType, String instanceId, boolean localizable,
		int priority) {

		DDMFieldModel ddmFieldModel = new DDMFieldModelImpl();

		// PK fields

		ddmFieldModel.setFieldId(_counter.get());

		// Audit fields

		ddmFieldModel.setCompanyId(_companyId);

		// Other fields

		ddmFieldModel.setParentFieldId(0);
		ddmFieldModel.setStorageId(storageId);
		ddmFieldModel.setStructureVersionId(structureVersionId);
		ddmFieldModel.setFieldName(fieldName);
		ddmFieldModel.setFieldType(fieldType);
		ddmFieldModel.setInstanceId(instanceId);
		ddmFieldModel.setLocalizable(localizable);
		ddmFieldModel.setPriority(priority);

		return ddmFieldModel;
	}

	protected DDMStorageLinkModel newDDMStorageLinkModel(
		long ddmStorageLinkId, long classPK, long structureId, long versionId) {

		DDMStorageLinkModel ddmStorageLinkModel = new DDMStorageLinkModelImpl();

		// PK fields

		ddmStorageLinkModel.setStorageLinkId(ddmStorageLinkId);

		// Audit fields

		ddmStorageLinkModel.setCompanyId(_companyId);

		// Other fields

		ddmStorageLinkModel.setClassNameId(getClassNameId(DDMContent.class));
		ddmStorageLinkModel.setClassPK(classPK);
		ddmStorageLinkModel.setStructureId(structureId);
		ddmStorageLinkModel.setStructureVersionId(versionId);

		// Autogenerated fields

		ddmStorageLinkModel.setUuid(SequentialUUID.generate());

		return ddmStorageLinkModel;
	}

	protected DDMStructureLayoutModel newDDMStructureLayoutModel(
		long groupId, long userId, long structureVersionId, String definition) {

		return newDDMStructureLayoutModel(
			groupId, userId, structureVersionId, definition, 0,
			String.valueOf(_counter.get()));
	}

	protected DDMStructureLayoutModel newDDMStructureLayoutModel(
		long groupId, long userId, long structureVersionId, String definition,
		long classNameId, String structureLayoutKey) {

		DDMStructureLayoutModel ddmStructureLayoutModel =
			new DDMStructureLayoutModelImpl();

		// PK fields

		ddmStructureLayoutModel.setStructureLayoutId(_counter.get());

		// Group instance

		ddmStructureLayoutModel.setGroupId(groupId);

		// Audit fields

		ddmStructureLayoutModel.setCompanyId(_companyId);
		ddmStructureLayoutModel.setUserId(userId);
		ddmStructureLayoutModel.setUserName(_SAMPLE_USER_NAME);
		ddmStructureLayoutModel.setCreateDate(nextFutureDate());
		ddmStructureLayoutModel.setModifiedDate(nextFutureDate());

		// Other fields

		ddmStructureLayoutModel.setClassNameId(classNameId);
		ddmStructureLayoutModel.setStructureLayoutKey(structureLayoutKey);
		ddmStructureLayoutModel.setStructureVersionId(structureVersionId);
		ddmStructureLayoutModel.setDefinition(definition);

		// Autogenerated fields

		ddmStructureLayoutModel.setUuid(SequentialUUID.generate());

		return ddmStructureLayoutModel;
	}

	protected DDMStructureLinkModel newDDMStructureLinkModel(
		long classNameId, long classPK, long structureId) {

		DDMStructureLinkModel ddmStructureLinkModel =
			new DDMStructureLinkModelImpl();

		// PK fields

		ddmStructureLinkModel.setStructureLinkId(_counter.get());

		// Audit fields

		ddmStructureLinkModel.setCompanyId(_companyId);

		// Other fields

		ddmStructureLinkModel.setClassNameId(classNameId);
		ddmStructureLinkModel.setClassPK(classPK);
		ddmStructureLinkModel.setStructureId(structureId);

		return ddmStructureLinkModel;
	}

	protected DDMStructureModel newDDMStructureModel(
		long groupId, long userId, long classNameId, String structureKey,
		String definition, long structureId) {

		DDMStructureModel ddmStructureModel = new DDMStructureModelImpl();

		// PK fields

		ddmStructureModel.setStructureId(structureId);

		// Group instance

		ddmStructureModel.setGroupId(groupId);

		// Audit fields

		ddmStructureModel.setCompanyId(_companyId);
		ddmStructureModel.setUserId(userId);
		ddmStructureModel.setUserName(_SAMPLE_USER_NAME);
		ddmStructureModel.setVersionUserId(userId);
		ddmStructureModel.setVersionUserName(_SAMPLE_USER_NAME);
		ddmStructureModel.setCreateDate(nextFutureDate());
		ddmStructureModel.setModifiedDate(nextFutureDate());

		// Other fields

		ddmStructureModel.setClassNameId(classNameId);
		ddmStructureModel.setStructureKey(structureKey);
		ddmStructureModel.setVersion(DDMStructureConstants.VERSION_DEFAULT);
		ddmStructureModel.setName(
			StringBundler.concat(
				"<?xml version=\"1.0\"?><root available-locales=\"en_US\" ",
				"default-locale=\"en_US\"><name language-id=\"en_US\">",
				structureKey, "</name></root>"));
		ddmStructureModel.setDefinition(definition);
		ddmStructureModel.setStorageType(StorageType.DEFAULT.toString());
		ddmStructureModel.setLastPublishDate(nextFutureDate());

		// Autogenerated fields

		ddmStructureModel.setUuid(SequentialUUID.generate());

		return ddmStructureModel;
	}

	protected DDMTemplateModel newDDMTemplateModel(
		long groupId, long userId, long structureId, long sourceClassNameId,
		long templateId) {

		return newDDMTemplateModel(
			groupId, userId, DDMTemplateConstants.TEMPLATE_MODE_CREATE,
			"Basic Web Content", "${content.getData()}",
			getClassNameId(DDMStructure.class), structureId, sourceClassNameId,
			templateId, _JOURNAL_STRUCTURE_KEY);
	}

	protected DDMTemplateModel newDDMTemplateModel(
		long groupId, long userId, String mode, String name, String script,
		long classNameId, long classPK, long resourceClassNameId,
		long templateId, String templateKey) {

		DDMTemplateModel ddmTemplateModel = new DDMTemplateModelImpl();

		// PK fields

		ddmTemplateModel.setTemplateId(templateId);

		// Group instance

		ddmTemplateModel.setGroupId(groupId);

		// Audit fields

		ddmTemplateModel.setCompanyId(_companyId);
		ddmTemplateModel.setUserId(userId);
		ddmTemplateModel.setVersionUserId(userId);
		ddmTemplateModel.setVersionUserName(_SAMPLE_USER_NAME);
		ddmTemplateModel.setCreateDate(nextFutureDate());
		ddmTemplateModel.setModifiedDate(nextFutureDate());

		// Other fields

		ddmTemplateModel.setClassNameId(classNameId);
		ddmTemplateModel.setClassPK(classPK);
		ddmTemplateModel.setResourceClassNameId(resourceClassNameId);
		ddmTemplateModel.setTemplateKey(templateKey);
		ddmTemplateModel.setVersion(DDMTemplateConstants.VERSION_DEFAULT);
		ddmTemplateModel.setName(
			StringBundler.concat(
				"<?xml version=\"1.0\"?><root available-locales=\"en_US\" ",
				"default-locale=\"en_US\"><name language-id=\"en_US\">", name,
				"</name></root>"));
		ddmTemplateModel.setType(DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY);
		ddmTemplateModel.setMode(mode);
		ddmTemplateModel.setLanguage(TemplateConstants.LANG_TYPE_FTL);
		ddmTemplateModel.setScript(script);
		ddmTemplateModel.setCacheable(true);
		ddmTemplateModel.setSmallImage(false);
		ddmTemplateModel.setLastPublishDate(nextFutureDate());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		ddmTemplateModel.setUuid(uuid);
		ddmTemplateModel.setExternalReferenceCode(uuid);

		return ddmTemplateModel;
	}

	protected DLFolderModel newDLFolderModel(
		long folderId, long groupId, long parentFolderId, String treePath,
		String name) {

		DLFolderModel dlFolderModel = new DLFolderModelImpl();

		// PK fields

		dlFolderModel.setFolderId(folderId);

		// Group instance

		dlFolderModel.setGroupId(groupId);

		// Audit fields

		dlFolderModel.setCompanyId(_companyId);
		dlFolderModel.setUserId(_sampleUserId);
		dlFolderModel.setUserName(_SAMPLE_USER_NAME);
		dlFolderModel.setCreateDate(nextFutureDate());
		dlFolderModel.setModifiedDate(nextFutureDate());

		// Other fields

		dlFolderModel.setRepositoryId(groupId);
		dlFolderModel.setParentFolderId(parentFolderId);
		dlFolderModel.setTreePath(treePath);
		dlFolderModel.setName(name);
		dlFolderModel.setLastPostDate(nextFutureDate());
		dlFolderModel.setDefaultFileEntryTypeId(_DEFAULT_DL_FILE_ENTRY_TYPE_ID);
		dlFolderModel.setLastPublishDate(nextFutureDate());
		dlFolderModel.setStatusDate(nextFutureDate());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		dlFolderModel.setUuid(uuid);
		dlFolderModel.setExternalReferenceCode(uuid);

		return dlFolderModel;
	}

	protected FragmentEntryLinkModel newFragmentEntryLinkModel(
			JournalArticleModel journalArticleModel, LayoutModel layoutModel,
			long segmentsExperienceId)
		throws Exception {

		FragmentEntryLinkModel fragmentEntryLinkModel =
			new FragmentEntryLinkModelImpl();

		// PK fields

		fragmentEntryLinkModel.setFragmentEntryLinkId(_counter.get());

		// Group instance

		fragmentEntryLinkModel.setGroupId(layoutModel.getGroupId());

		// Audit fields

		fragmentEntryLinkModel.setCompanyId(_companyId);
		fragmentEntryLinkModel.setUserId(_sampleUserId);
		fragmentEntryLinkModel.setUserName(_SAMPLE_USER_NAME);
		fragmentEntryLinkModel.setCreateDate(new Date());
		fragmentEntryLinkModel.setModifiedDate(new Date());

		// Other fields

		fragmentEntryLinkModel.setFragmentEntryId(0);
		fragmentEntryLinkModel.setSegmentsExperienceId(segmentsExperienceId);
		fragmentEntryLinkModel.setClassNameId(getClassNameId(Layout.class));
		fragmentEntryLinkModel.setClassPK(layoutModel.getPlid());
		fragmentEntryLinkModel.setPlid(layoutModel.getPlid());
		fragmentEntryLinkModel.setCss(
			_readFile(_getFragmentComponentInputStream("heading", "css")));
		fragmentEntryLinkModel.setHtml(
			_readFile(_getFragmentComponentInputStream("heading", "html")));
		fragmentEntryLinkModel.setJs(StringPool.BLANK);
		fragmentEntryLinkModel.setConfiguration(
			_readFile(
				"fragment_component/fragment_component_heading_configuration." +
					"json"));

		fragmentEntryLinkModel.setEditableValues(
			StringUtil.replace(
				StringUtil.replace(
					StringUtil.replace(
						_readFile(
							"fragment_component/fragment_" +
								"component_heading_editValue_attachment_" +
									"journal_article_title.json"),
						"${journalArticleClassNameId}",
						String.valueOf(getClassNameId(JournalArticle.class))),
					"${journalArticleClassPK}",
					String.valueOf(journalArticleModel.getResourcePrimKey())),
				"${journalArticleTitle}", journalArticleModel.getUrlTitle()));

		fragmentEntryLinkModel.setNamespace(StringUtil.randomId());
		fragmentEntryLinkModel.setPosition(0);
		fragmentEntryLinkModel.setRendererKey(
			_FRAGMENT_COMPONENT_RENDER_KEY_HEADING);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		fragmentEntryLinkModel.setUuid(uuid);
		fragmentEntryLinkModel.setExternalReferenceCode(uuid);

		return fragmentEntryLinkModel;
	}

	protected FragmentEntryLinkModel newFragmentEntryLinkModel(
		LayoutModel layoutModel, long originalFragmentEntryLinkId,
		long segmentsExperienceId, String css, String html,
		String configuration, String editValue, String nameSpace, int position,
		String renderKey) {

		FragmentEntryLinkModel fragmentEntryLinkModel =
			new FragmentEntryLinkModelImpl();

		// PK fields

		fragmentEntryLinkModel.setFragmentEntryLinkId(_counter.get());

		// Group instance

		fragmentEntryLinkModel.setGroupId(layoutModel.getGroupId());

		// Audit fields

		fragmentEntryLinkModel.setCompanyId(_companyId);
		fragmentEntryLinkModel.setUserId(_sampleUserId);
		fragmentEntryLinkModel.setUserName(_SAMPLE_USER_NAME);
		fragmentEntryLinkModel.setCreateDate(new Date());
		fragmentEntryLinkModel.setModifiedDate(new Date());

		// Other fields

		fragmentEntryLinkModel.setOriginalFragmentEntryLinkId(
			originalFragmentEntryLinkId);
		fragmentEntryLinkModel.setFragmentEntryId(0);
		fragmentEntryLinkModel.setSegmentsExperienceId(segmentsExperienceId);
		fragmentEntryLinkModel.setClassNameId(getClassNameId(Layout.class));
		fragmentEntryLinkModel.setClassPK(layoutModel.getPlid());
		fragmentEntryLinkModel.setPlid(layoutModel.getPlid());
		fragmentEntryLinkModel.setCss(css);
		fragmentEntryLinkModel.setHtml(html);
		fragmentEntryLinkModel.setConfiguration(configuration);
		fragmentEntryLinkModel.setEditableValues(editValue);
		fragmentEntryLinkModel.setNamespace(nameSpace);
		fragmentEntryLinkModel.setPosition(position);
		fragmentEntryLinkModel.setRendererKey(renderKey);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		fragmentEntryLinkModel.setUuid(uuid);
		fragmentEntryLinkModel.setExternalReferenceCode(uuid);

		return fragmentEntryLinkModel;
	}

	protected GroupModel newGroupModel(
		long classNameId, long classPK, long groupId, String name,
		boolean site) {

		return newGroupModel(
			classNameId, classPK, groupId, name, 0, StringPool.BLANK, site);
	}

	protected GroupModel newGroupModel(
		long classNameId, long classPK, long groupId, String name, int type,
		String typeSettings, boolean site) {

		return newGroupModel(
			classNameId, classPK, name, groupId, name, name, site, type,
			typeSettings, _sampleUserId);
	}

	protected GroupModel newGroupModel(
		long classNameId, long classPK, String friendlyURL, long groupId,
		String groupKey, String name, boolean site, int type,
		String typeSettings, long userId) {

		GroupModel groupModel = new GroupModelImpl();

		// PK fields

		groupModel.setGroupId(groupId);

		// Audit fields

		groupModel.setCompanyId(_companyId);
		groupModel.setCreatorUserId(userId);

		// Other fields

		groupModel.setClassNameId(classNameId);
		groupModel.setClassPK(classPK);
		groupModel.setTreePath(
			StringPool.SLASH + groupModel.getGroupId() + StringPool.SLASH);
		groupModel.setGroupKey(groupKey);
		groupModel.setName(name);
		groupModel.setType(type);
		groupModel.setTypeSettings(typeSettings);
		groupModel.setManualMembership(true);
		groupModel.setMembershipRestriction(
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION);
		groupModel.setFriendlyURL(
			StringPool.FORWARD_SLASH +
				_friendlyURLNormalizer.normalize(friendlyURL));
		groupModel.setSite(site);
		groupModel.setActive(true);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		groupModel.setUuid(uuid);
		groupModel.setExternalReferenceCode(uuid);

		return groupModel;
	}

	protected LayoutModel newLayoutModel(
		long groupId, boolean hidden, String layoutTemplateId, String name,
		boolean privateLayout, long parentLayoutId, String... columns) {

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.create(
				true
			).put(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, layoutTemplateId
			).build();

		for (int i = 0; i < columns.length; i++) {
			if (!columns[i].equals("")) {
				typeSettingsUnicodeProperties.setProperty(
					"column-" + (i + 1), columns[i]);
			}
		}

		if (name.equals("search")) {
			typeSettingsUnicodeProperties.setProperty("privateLayout", "true");
		}
		else {
			typeSettingsUnicodeProperties.setProperty(
				"privateLayout", String.valueOf(privateLayout));
		}

		return newLayoutModel(
			name, groupId, hidden, name, privateLayout, parentLayoutId,
			typeSettingsUnicodeProperties.toString());
	}

	protected LayoutModel newLayoutModel(
		long groupId, String layoutTemplateId, String name,
		boolean privateLayout, String... columns) {

		return newLayoutModel(
			groupId, false, layoutTemplateId, name, privateLayout, 0, columns);
	}

	protected LayoutModel newLayoutModel(
		String friendlyURL, long groupId, boolean hidden, String name,
		boolean privateLayout, long parentLayoutId, String typeSettings) {

		SimpleCounter simpleCounter = _layoutIdCounters.computeIfAbsent(
			LayoutLocalServiceImpl.getCounterName(groupId, privateLayout),
			counterName -> new SimpleCounter());

		LayoutModel layoutModel = new LayoutModelImpl();

		// PK fields

		layoutModel.setPlid(_layoutPlidCounter.get());

		// Group instance

		layoutModel.setGroupId(groupId);

		// Audit fields

		layoutModel.setCompanyId(_companyId);
		layoutModel.setUserId(_sampleUserId);
		layoutModel.setUserName(_SAMPLE_USER_NAME);
		layoutModel.setCreateDate(new Date());
		layoutModel.setModifiedDate(new Date());

		// Other fields

		layoutModel.setLayoutId(simpleCounter.get());
		layoutModel.setParentLayoutId(parentLayoutId);
		layoutModel.setPrivateLayout(privateLayout);
		layoutModel.setName(
			"<?xml version=\"1.0\"?><root><name>" + name + "</name></root>");
		layoutModel.setType(LayoutConstants.TYPE_PORTLET);
		layoutModel.setHidden(hidden);

		layoutModel.setTypeSettings(
			StringUtil.replace(typeSettings, '\n', "\\n"));

		layoutModel.setFriendlyURL(StringPool.FORWARD_SLASH + friendlyURL);
		layoutModel.setLastPublishDate(new Date());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		layoutModel.setUuid(uuid);
		layoutModel.setExternalReferenceCode(uuid);

		return layoutModel;
	}

	protected LayoutSetModel newLayoutSetModel(
		long groupId, boolean privateLayout) {

		return newLayoutSetModel(
			groupId, privateLayout, "classic_WAR_classictheme");
	}

	protected LayoutSetModel newLayoutSetModel(
		long groupId, boolean privateLayout, String themeId) {

		LayoutSetModel layoutSetModel = new LayoutSetModelImpl();

		// PK fields

		layoutSetModel.setLayoutSetId(_layoutSetIdCounter.get());

		// Group instance

		layoutSetModel.setGroupId(groupId);

		// Audit fields

		layoutSetModel.setCompanyId(_companyId);
		layoutSetModel.setCreateDate(new Date());
		layoutSetModel.setModifiedDate(new Date());

		// Other fields

		layoutSetModel.setPrivateLayout(privateLayout);
		layoutSetModel.setThemeId(themeId);
		layoutSetModel.setColorSchemeId("01");

		return layoutSetModel;
	}

	protected ListTypeEntryModel newListTypeEntryModel(
		long listTypeDefinitionId) {

		ListTypeEntryModel listTypeEntryModel = new ListTypeEntryModelImpl();

		// PK fields

		listTypeEntryModel.setListTypeEntryId(_counter.get());

		// Audit fields

		listTypeEntryModel.setCompanyId(_companyId);
		listTypeEntryModel.setUserId(_sampleUserId);
		listTypeEntryModel.setUserName(_SAMPLE_USER_NAME);
		listTypeEntryModel.setCreateDate(new Date());
		listTypeEntryModel.setModifiedDate(new Date());

		// Other field

		listTypeEntryModel.setListTypeDefinitionId(listTypeDefinitionId);

		String key =
			"picklist_value_" + listTypeEntryModel.getListTypeEntryId();

		_defaultListTypeEntryKey = key;

		listTypeEntryModel.setKey(key);
		listTypeEntryModel.setName(
			StringBundler.concat(
				"<?xml version=\"1.0\"?><root available-locales=\"en_US\" ",
				"default-locale=\"en_US\"><name language-id=\"en_US\">", key,
				"</name></root>"));

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		listTypeEntryModel.setUuid(uuid);
		listTypeEntryModel.setExternalReferenceCode(uuid);

		return listTypeEntryModel;
	}

	protected MBCategoryModel newMBCategoryModel(long groupId, int index) {
		MBCategoryModel mbCategoryModel = new MBCategoryModelImpl();

		// PK fields

		mbCategoryModel.setCategoryId(_counter.get());

		// Group instance

		mbCategoryModel.setGroupId(groupId);

		// Audit fields

		mbCategoryModel.setCompanyId(_companyId);
		mbCategoryModel.setUserId(_sampleUserId);
		mbCategoryModel.setUserName(_SAMPLE_USER_NAME);
		mbCategoryModel.setCreateDate(new Date());
		mbCategoryModel.setModifiedDate(new Date());

		// Other fields

		mbCategoryModel.setParentCategoryId(
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);

		String name = "Test Category " + index;

		mbCategoryModel.setName(name);

		mbCategoryModel.setDisplayStyle(
			MBCategoryConstants.DEFAULT_DISPLAY_STYLE);
		mbCategoryModel.setFriendlyURL(
			StringUtil.replace(name, CharPool.SPACE, StringPool.DASH));
		mbCategoryModel.setLastPublishDate(new Date());
		mbCategoryModel.setStatusDate(new Date());

		// Autogenerated fields

		mbCategoryModel.setUuid(SequentialUUID.generate());

		return mbCategoryModel;
	}

	protected MBMessageModel newMBMessageModel(
		long groupId, long classNameId, long classPK, long categoryId,
		long threadId, long messageId, long rootMessageId, long parentMessageId,
		String subject, String urlSubject, String body) {

		MBMessageModel mBMessageModel = new MBMessageModelImpl();

		// PK fields

		mBMessageModel.setMessageId(messageId);

		// Group instance

		mBMessageModel.setGroupId(groupId);

		// Audit fields

		mBMessageModel.setCompanyId(_companyId);
		mBMessageModel.setUserId(_sampleUserId);
		mBMessageModel.setUserName(_SAMPLE_USER_NAME);
		mBMessageModel.setCreateDate(new Date());
		mBMessageModel.setModifiedDate(new Date());

		// Other fields

		mBMessageModel.setClassNameId(classNameId);
		mBMessageModel.setClassPK(classPK);
		mBMessageModel.setCategoryId(categoryId);
		mBMessageModel.setThreadId(threadId);
		mBMessageModel.setRootMessageId(rootMessageId);
		mBMessageModel.setParentMessageId(parentMessageId);
		mBMessageModel.setSubject(subject);
		mBMessageModel.setUrlSubject(urlSubject + "-" + messageId);
		mBMessageModel.setBody(body);
		mBMessageModel.setFormat(MBMessageConstants.DEFAULT_FORMAT);
		mBMessageModel.setLastPublishDate(new Date());
		mBMessageModel.setStatusDate(new Date());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		mBMessageModel.setUuid(uuid);
		mBMessageModel.setExternalReferenceCode(uuid);

		return mBMessageModel;
	}

	protected MBThreadModel newMBThreadModel(
		long threadId, long groupId, long categoryId, long rootMessageId) {

		MBThreadModel mbThreadModel = new MBThreadModelImpl();

		// PK fields

		mbThreadModel.setThreadId(threadId);

		// Group instance

		mbThreadModel.setGroupId(groupId);

		// Audit fields

		mbThreadModel.setCompanyId(_companyId);
		mbThreadModel.setUserId(_sampleUserId);
		mbThreadModel.setUserName(_SAMPLE_USER_NAME);
		mbThreadModel.setCreateDate(new Date());
		mbThreadModel.setModifiedDate(new Date());

		// Other fields

		mbThreadModel.setCategoryId(categoryId);
		mbThreadModel.setRootMessageId(rootMessageId);
		mbThreadModel.setRootMessageUserId(_sampleUserId);
		mbThreadModel.setLastPostByUserId(_sampleUserId);
		mbThreadModel.setLastPostDate(new Date());
		mbThreadModel.setLastPublishDate(new Date());
		mbThreadModel.setStatusDate(new Date());

		// Autogenerated fields

		mbThreadModel.setUuid(SequentialUUID.generate());

		return mbThreadModel;
	}

	protected ObjectDefinitionModel newObjectDefinitionModel(
		long objectDefinitionId, long objectFolderId, long titleObjectFieldId,
		String className, String dbTableName, boolean enableComments,
		boolean enableIndexSearch, boolean enableObjectEntryHistory,
		String label, boolean modifiable, String name, String panelCategoryKey,
		String pkObjectFieldDBColumnName, String pkObjectFieldName,
		String pluralLabel, boolean portlet, boolean system,
		String externalReferenceCode, String uuid) {

		ObjectDefinitionModel objectDefinitionModel =
			new ObjectDefinitionImpl();

		// PK fields

		objectDefinitionModel.setObjectDefinitionId(objectDefinitionId);

		if (StringUtil.equals(dbTableName, "User_")) {
			_objectDefinitionId = objectDefinitionId;
		}

		// Audit fields

		objectDefinitionModel.setCompanyId(_companyId);
		objectDefinitionModel.setUserId(_sampleUserId);
		objectDefinitionModel.setUserName(_SAMPLE_USER_NAME);
		objectDefinitionModel.setCreateDate(new Date());
		objectDefinitionModel.setModifiedDate(new Date());

		// Other fields

		objectDefinitionModel.setDescriptionObjectFieldId(0);
		objectDefinitionModel.setObjectFolderId(objectFolderId);
		objectDefinitionModel.setTitleObjectFieldId(titleObjectFieldId);
		objectDefinitionModel.setAccountEntryRestricted(false);
		objectDefinitionModel.setActive(true);
		objectDefinitionModel.setClassName(className);
		objectDefinitionModel.setDBTableName(dbTableName);
		objectDefinitionModel.setEnableCategorization(true);
		objectDefinitionModel.setEnableComments(enableComments);
		objectDefinitionModel.setEnableIndexSearch(enableIndexSearch);
		objectDefinitionModel.setEnableLocalization(false);
		objectDefinitionModel.setEnableObjectEntryDraft(false);
		objectDefinitionModel.setEnableObjectEntryHistory(
			enableObjectEntryHistory);
		objectDefinitionModel.setLabel(label);
		objectDefinitionModel.setModifiable(modifiable);
		objectDefinitionModel.setName(name);
		objectDefinitionModel.setPanelAppOrder(null);
		objectDefinitionModel.setPanelCategoryKey(panelCategoryKey);
		objectDefinitionModel.setPKObjectFieldDBColumnName(
			pkObjectFieldDBColumnName);
		objectDefinitionModel.setPKObjectFieldName(pkObjectFieldName);
		objectDefinitionModel.setPluralLabel(pluralLabel);
		objectDefinitionModel.setPortlet(portlet);
		objectDefinitionModel.setScope(ObjectDefinitionConstants.SCOPE_COMPANY);
		objectDefinitionModel.setStorageType(
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT);
		objectDefinitionModel.setSystem(system);
		objectDefinitionModel.setVersion(0);
		objectDefinitionModel.setStatus(WorkflowConstants.STATUS_APPROVED);

		// Autogenerated fields

		objectDefinitionModel.setExternalReferenceCode(externalReferenceCode);
		objectDefinitionModel.setUuid(uuid);

		return objectDefinitionModel;
	}

	protected ObjectEntryModel newObjectEntryModel(long objectDefinitionId) {
		ObjectEntryModel objectEntryModel = new ObjectEntryModelImpl();

		// PK fields

		objectEntryModel.setObjectEntryId(_counter.get());

		// Group instance

		objectEntryModel.setGroupId(0);

		// Audit fields

		objectEntryModel.setCompanyId(_companyId);
		objectEntryModel.setUserId(_sampleUserId);
		objectEntryModel.setUserName(_SAMPLE_USER_NAME);
		objectEntryModel.setCreateDate(new Date());
		objectEntryModel.setModifiedDate(new Date());

		// Other fields

		objectEntryModel.setObjectDefinitionId(objectDefinitionId);
		objectEntryModel.setStatus(WorkflowConstants.STATUS_APPROVED);
		objectEntryModel.setStatusByUserId(_sampleUserId);
		objectEntryModel.setStatusByUserName(_SAMPLE_USER_NAME);
		objectEntryModel.setStatusDate(new Date());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		objectEntryModel.setExternalReferenceCode(uuid);
		objectEntryModel.setUuid(uuid);

		return objectEntryModel;
	}

	protected ObjectStateModel newObjectStateModel(
		long listTypeEntryId, long objectStateFlowId) {

		ObjectStateModel objectStateModel = new ObjectStateModelImpl();

		// PK fields

		objectStateModel.setObjectStateId(_counter.get());

		// Audit fields

		objectStateModel.setCompanyId(_companyId);
		objectStateModel.setUserId(_sampleUserId);
		objectStateModel.setUserName(_SAMPLE_USER_NAME);
		objectStateModel.setCreateDate(new Date());
		objectStateModel.setModifiedDate(new Date());

		// Other fields

		objectStateModel.setListTypeEntryId(listTypeEntryId);
		objectStateModel.setObjectStateFlowId(objectStateFlowId);

		return objectStateModel;
	}

	protected ObjectStateTransitionModel newObjectStateTransitionModel(
		long objectStateFlowId, long sourceObjectStateId,
		long targetObjectStateId) {

		ObjectStateTransitionModel objectStateTransitionModel =
			new ObjectStateTransitionModelImpl();

		// PK fields

		objectStateTransitionModel.setObjectStateTransitionId(_counter.get());

		// Audit fields

		objectStateTransitionModel.setCompanyId(_companyId);
		objectStateTransitionModel.setUserId(_sampleUserId);
		objectStateTransitionModel.setUserName(_SAMPLE_USER_NAME);
		objectStateTransitionModel.setCreateDate(new Date());
		objectStateTransitionModel.setModifiedDate(new Date());

		// Other fields

		objectStateTransitionModel.setObjectStateFlowId(objectStateFlowId);
		objectStateTransitionModel.setSourceObjectStateId(sourceObjectStateId);
		objectStateTransitionModel.setTargetObjectStateId(targetObjectStateId);

		return objectStateTransitionModel;
	}

	protected PortletPreferenceValueModel newPortletPreferenceValueModel(
		PortletPreferencesModel portletPreferencesModel, String name, int index,
		String value) {

		PortletPreferenceValueModel portletPreferenceValueModel =
			new PortletPreferenceValueModelImpl();

		// PK fields

		portletPreferenceValueModel.setPortletPreferenceValueId(
			_portletPreferenceValueIdCounter.get());

		//  Audit fields

		portletPreferenceValueModel.setCompanyId(_companyId);

		// Other fields

		portletPreferenceValueModel.setPortletPreferencesId(
			portletPreferencesModel.getPortletPreferencesId());
		portletPreferenceValueModel.setIndex(index);
		portletPreferenceValueModel.setName(name);

		if (value.length() >
				PortletPreferenceValueImpl.SMALL_VALUE_MAX_LENGTH) {

			portletPreferenceValueModel.setLargeValue(value);
		}
		else {
			portletPreferenceValueModel.setSmallValue(value);
		}

		return portletPreferenceValueModel;
	}

	protected ReleaseModelImpl newReleaseModel(
			long releaseId, String servletContextName, String schemaVersion,
			int buildNumber, boolean verified, String testString)
		throws IOException {

		ReleaseModelImpl releaseModelImpl = new ReleaseModelImpl();

		// PK fields

		releaseModelImpl.setReleaseId(releaseId);

		// Audit fields

		releaseModelImpl.setCreateDate(new Date());
		releaseModelImpl.setModifiedDate(new Date());

		// Other fields

		releaseModelImpl.setServletContextName(servletContextName);
		releaseModelImpl.setSchemaVersion(schemaVersion);
		releaseModelImpl.setBuildNumber(buildNumber);
		releaseModelImpl.setBuildDate(new Date());
		releaseModelImpl.setVerified(verified);
		releaseModelImpl.setTestString(testString);

		return releaseModelImpl;
	}

	protected ResourcePermissionModel newResourcePermissionModel(
		String name, String primKey, long roleId, long ownerId) {

		ResourcePermissionModel resourcePermissionModel =
			new ResourcePermissionModelImpl();

		// PK fields

		resourcePermissionModel.setResourcePermissionId(
			_resourcePermissionIdCounter.get());

		// Audit fields

		resourcePermissionModel.setCompanyId(_companyId);

		// Other fields

		resourcePermissionModel.setName(name);
		resourcePermissionModel.setScope(ResourceConstants.SCOPE_INDIVIDUAL);
		resourcePermissionModel.setPrimKey(primKey);
		resourcePermissionModel.setPrimKeyId(GetterUtil.getLong(primKey));
		resourcePermissionModel.setRoleId(roleId);
		resourcePermissionModel.setOwnerId(ownerId);
		resourcePermissionModel.setActionIds(1);
		resourcePermissionModel.setViewActionId(true);

		return resourcePermissionModel;
	}

	protected List<ResourcePermissionModel> newResourcePermissionModels(
		String name, String primKey, long ownerId) {

		return ListUtil.fromArray(
			newResourcePermissionModel(
				name, primKey, _guestRoleModel.getRoleId(), 0),
			newResourcePermissionModel(
				name, primKey, _ownerRoleModel.getRoleId(), ownerId),
			newResourcePermissionModel(
				name, primKey, _siteMemberRoleModel.getRoleId(), 0));
	}

	protected RoleModel newRoleModel(String name, int type) {
		RoleModel roleModel = new RoleModelImpl();

		// PK fields

		roleModel.setRoleId(_counter.get());

		// Audit fields

		roleModel.setCompanyId(_companyId);
		roleModel.setUserId(_sampleUserId);
		roleModel.setUserName(_SAMPLE_USER_NAME);
		roleModel.setCreateDate(new Date());
		roleModel.setModifiedDate(new Date());

		// Other fields

		roleModel.setClassNameId(getClassNameId(Role.class));
		roleModel.setClassPK(roleModel.getRoleId());
		roleModel.setName(name);
		roleModel.setType(type);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		roleModel.setUuid(uuid);
		roleModel.setExternalReferenceCode(uuid);

		return roleModel;
	}

	protected SegmentsExperienceModel newSegmentsExperienceModel(
		long groupId, long segmentsEntryId, String segmentsExperienceKey,
		long plid, String name, int priority) {

		SegmentsExperienceModel segmentsExperienceModel =
			new SegmentsExperienceModelImpl();

		// PK fields

		segmentsExperienceModel.setSegmentsExperienceId(_counter.get());

		// Group instance

		segmentsExperienceModel.setGroupId(groupId);

		// Audit fields

		segmentsExperienceModel.setCompanyId(_companyId);
		segmentsExperienceModel.setUserId(_sampleUserId);
		segmentsExperienceModel.setUserName(_SAMPLE_USER_NAME);
		segmentsExperienceModel.setCreateDate(new Date());
		segmentsExperienceModel.setModifiedDate(new Date());

		// Other fields

		segmentsExperienceModel.setSegmentsEntryId(segmentsEntryId);
		segmentsExperienceModel.setSegmentsExperienceKey(segmentsExperienceKey);
		segmentsExperienceModel.setPlid(plid);
		segmentsExperienceModel.setName(
			StringBundler.concat(
				"<?xml version=\"1.0\"?><root available-locales=\"en_US\" ",
				"default-locale=\"en_US\"><Name language-id=\"en_US\">", name,
				"</Name></root>"));
		segmentsExperienceModel.setPriority(priority);
		segmentsExperienceModel.setActive(true);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		segmentsExperienceModel.setUuid(uuid);
		segmentsExperienceModel.setExternalReferenceCode(uuid);

		return segmentsExperienceModel;
	}

	protected SocialActivityModel newSocialActivityModel(
		long groupId, long classNameId, long classPK, int type,
		String extraData) {

		SocialActivityModel socialActivityModel = new SocialActivityModelImpl();

		// PK fields

		socialActivityModel.setActivityId(_socialActivityIdCounter.get());

		// Group instance

		socialActivityModel.setGroupId(groupId);

		// Audit fields

		socialActivityModel.setCompanyId(_companyId);
		socialActivityModel.setUserId(_sampleUserId);
		socialActivityModel.setCreateDate(_CURRENT_TIME + _timeCounter.get());

		// Other fields

		socialActivityModel.setClassNameId(classNameId);
		socialActivityModel.setClassPK(classPK);
		socialActivityModel.setType(type);
		socialActivityModel.setExtraData(extraData);

		return socialActivityModel;
	}

	protected SubscriptionModel newSubscriptionModel(
		long classNameId, long classPK) {

		SubscriptionModel subscriptionModel = new SubscriptionModelImpl();

		// PK fields

		subscriptionModel.setSubscriptionId(_counter.get());

		// Audit fields

		subscriptionModel.setCompanyId(_companyId);
		subscriptionModel.setUserId(_sampleUserId);
		subscriptionModel.setUserName(_SAMPLE_USER_NAME);
		subscriptionModel.setCreateDate(new Date());
		subscriptionModel.setModifiedDate(new Date());

		// Other fields

		subscriptionModel.setClassNameId(classNameId);
		subscriptionModel.setClassPK(classPK);
		subscriptionModel.setFrequency(SubscriptionConstants.FREQUENCY_INSTANT);

		return subscriptionModel;
	}

	protected UserModel newUserModel(
		long userId, String firstName, String lastName, String screenName,
		int type) {

		String emailAddress = screenName + "@liferay.com";

		if (Validator.isNull(screenName)) {
			screenName = String.valueOf(userId);
			emailAddress = "default@liferay.com";
		}

		UserModel userModel = new UserModelImpl();

		// PK fields

		userModel.setUserId(userId);

		// Audit fields

		userModel.setCompanyId(_companyId);
		userModel.setCreateDate(new Date());
		userModel.setModifiedDate(new Date());

		// Other fields

		userModel.setContactId(_counter.get());
		userModel.setPassword("test");
		userModel.setPasswordModifiedDate(new Date());
		userModel.setReminderQueryQuestion("What is your screen name?");
		userModel.setReminderQueryAnswer(screenName);
		userModel.setScreenName(screenName);
		userModel.setEmailAddress(emailAddress);
		userModel.setLanguageId("en_US");
		userModel.setGreeting("Welcome " + screenName + StringPool.EXCLAMATION);
		userModel.setFirstName(firstName);
		userModel.setLastName(lastName);
		userModel.setLoginDate(new Date());
		userModel.setLastLoginDate(new Date());
		userModel.setLastFailedLoginDate(new Date());
		userModel.setLockoutDate(new Date());
		userModel.setAgreedToTermsOfUse(true);
		userModel.setEmailAddressVerified(true);
		userModel.setType(type);

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		userModel.setUuid(uuid);
		userModel.setExternalReferenceCode(uuid);

		return userModel;
	}

	protected String nextDDLCustomFieldName(
		long groupId, int customFieldIndex) {

		return StringBundler.concat(
			"custom_field_text_", groupId, "_", customFieldIndex);
	}

	protected Date nextFutureDate() {
		return new Date(
			_FUTURE_TIME + (_futureDateCounter.get() * Time.SECOND));
	}

	protected void toInsertSQL(StringBundler sb, BaseModel<?> baseModel) {
		try {
			sb.append("insert into ");

			Class<?> clazz = baseModel.getClass();

			Field tableNameField = clazz.getField("TABLE_NAME");

			sb.append(tableNameField.get(null));

			sb.append(" values (");

			Field tableColumnsField = clazz.getField("TABLE_COLUMNS");

			for (Object[] tableColumn :
					(Object[][])tableColumnsField.get(null)) {

				String name = TextFormatter.format(
					(String)tableColumn[0], TextFormatter.G);

				if (name.endsWith(StringPool.UNDERLINE)) {
					name = name.substring(0, name.length() - 1);
				}
				else if (name.equals("AccountERObjectFieldId")) {
					name = "AccountEntryRestrictedObjectFieldId";
				}
				else if (name.equals("AlEntrySegmentsEntryRelId")) {
					name = "AssetListEntrySegmentsEntryRelId";
				}
				else if (name.equals("CdnEnabled")) {
					name = "CDNEnabled";
				}
				else if (name.equals("CdnURL")) {
					name = "CDNURL";
				}
				else if (name.equals("CmExternalReferenceCode")) {
					name = "ClassedModelExternalReferenceCode";
				}
				else if (name.equals("CIBookedQuantityId")) {
					name = "CommerceInventoryBookedQuantityId";
				}
				else if (name.equals("CIWarehouseId")) {
					name = "CommerceInventoryWarehouseId";
				}
				else if (name.equals("CIWarehouseItemId")) {
					name = "CommerceInventoryWarehouseItemId";
				}
				else if (name.equals("CPDSpecificationOptionValueId")) {
					name = "CPDefinitionSpecificationOptionValueId";
				}
				else if (name.equals("DbColumnName")) {
					name = "DBColumnName";
				}
				else if (name.equals("DbTableName")) {
					name = "DBTableName";
				}
				else if (name.equals("DbType")) {
					name = "DBType";
				}
				else if (name.equals("DeliveryCTermEntryDescription")) {
					name = "DeliveryCommerceTermEntryDescription";
				}
				else if (name.equals("DeliverySubTypeSettings")) {
					name = "DeliverySubscriptionTypeSettings";
				}
				else if (name.equals("DiscountPctLevel1WithTaxAmount")) {
					name = "DiscountPercentageLevel1WithTaxAmount";
				}
				else if (name.equals("DiscountPctLevel2WithTaxAmount")) {
					name = "DiscountPercentageLevel2WithTaxAmount";
				}
				else if (name.equals("DiscountPctLevel3WithTaxAmount")) {
					name = "DiscountPercentageLevel3WithTaxAmount";
				}
				else if (name.equals("DiscountPctLevel4WithTaxAmount")) {
					name = "DiscountPercentageLevel4WithTaxAmount";
				}
				else if (name.equals("LPageTemplateStructureRelId")) {
					name = "LayoutPageTemplateStructureRelId";
				}
				else if (name.equals("PaymentCTermEntryDescription")) {
					name = "PaymentCommerceTermEntryDescription";
				}
				else if (name.equals("PkObjectFieldDBColumnName")) {
					name = "PKObjectFieldDBColumnName";
				}
				else if (name.equals("PkObjectFieldName")) {
					name = "PKObjectFieldName";
				}
				else if (name.equals("ShippingDiscountPctLev1WithTax")) {
					name = "ShippingDiscountPercentageLevel1WithTaxAmount";
				}
				else if (name.equals("ShippingDiscountPctLev2WithTax")) {
					name = "ShippingDiscountPercentageLevel2WithTaxAmount";
				}
				else if (name.equals("ShippingDiscountPctLev3WithTax")) {
					name = "ShippingDiscountPercentageLevel3WithTaxAmount";
				}
				else if (name.equals("ShippingDiscountPctLev4WithTax")) {
					name = "ShippingDiscountPercentageLevel4WithTaxAmount";
				}
				else if (name.equals("ShippingDiscountPercentLevel1")) {
					name = "ShippingDiscountPercentageLevel1";
				}
				else if (name.equals("ShippingDiscountPercentLevel2")) {
					name = "ShippingDiscountPercentageLevel2";
				}
				else if (name.equals("ShippingDiscountPercentLevel3")) {
					name = "ShippingDiscountPercentageLevel3";
				}
				else if (name.equals("ShippingDiscountPercentLevel4")) {
					name = "ShippingDiscountPercentageLevel4";
				}
				else if (name.equals("SubtotalDiscountPctLev1WithTax")) {
					name = "SubtotalDiscountPercentageLevel1WithTaxAmount";
				}
				else if (name.equals("SubtotalDiscountPctLev2WithTax")) {
					name = "SubtotalDiscountPercentageLevel2WithTaxAmount";
				}
				else if (name.equals("SubtotalDiscountPctLev3WithTax")) {
					name = "SubtotalDiscountPercentageLevel3WithTaxAmount";
				}
				else if (name.equals("SubtotalDiscountPctLev4WithTax")) {
					name = "SubtotalDiscountPercentageLevel4WithTaxAmount";
				}
				else if (name.equals("SubtotalDiscountPercentLevel1")) {
					name = "SubtotalDiscountPercentageLevel1";
				}
				else if (name.equals("SubtotalDiscountPercentLevel2")) {
					name = "SubtotalDiscountPercentageLevel2";
				}
				else if (name.equals("SubtotalDiscountPercentLevel3")) {
					name = "SubtotalDiscountPercentageLevel3";
				}
				else if (name.equals("SubtotalDiscountPercentLevel4")) {
					name = "SubtotalDiscountPercentageLevel4";
				}
				else if (name.equals("TotalDiscountPctLev1WithTax")) {
					name = "TotalDiscountPercentageLevel1WithTaxAmount";
				}
				else if (name.equals("TotalDiscountPctLev2WithTax")) {
					name = "TotalDiscountPercentageLevel2WithTaxAmount";
				}
				else if (name.equals("TotalDiscountPctLev3WithTax")) {
					name = "TotalDiscountPercentageLevel3WithTaxAmount";
				}
				else if (name.equals("TotalDiscountPctLev4WithTax")) {
					name = "TotalDiscountPercentageLevel4WithTaxAmount";
				}
				else if (name.equals("UOMIncrementalOrderQuantity")) {
					name = "UnitOfMeasureIncrementalOrderQuantity";
				}

				int type = (int)tableColumn[1];

				if (type == Types.TIMESTAMP) {
					Method method = clazz.getMethod("get".concat(name));

					Date date = (Date)method.invoke(baseModel);

					if (date == null) {
						sb.append("null");
					}
					else {
						sb.append("'");
						sb.append(_simpleDateFormat.format(date));
						sb.append("'");
					}
				}
				else if ((type == Types.VARCHAR) || (type == Types.CLOB)) {
					Method method = clazz.getMethod("get".concat(name));

					sb.append("'");
					sb.append(method.invoke(baseModel));
					sb.append("'");
				}
				else if (type == Types.BOOLEAN) {
					Method method = clazz.getMethod("is".concat(name));

					sb.append(method.invoke(baseModel));
				}
				else {
					Method method = clazz.getMethod("get".concat(name));

					sb.append(method.invoke(baseModel));
				}

				sb.append(", ");
			}

			sb.setIndex(sb.index() - 1);

			sb.append(");");
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			ReflectionUtil.throwException(reflectiveOperationException);
		}
	}

	private String _generateData(
			List<FragmentEntryLinkModel> fragmentEntryLinkModels)
		throws Exception {

		Map<String, String> itemPageLayoutDefinitions = new LinkedHashMap<>();

		String itemPageLayoutDefinitionTemplate = _readFile(
			"content_page_template/item-page-layout-definition-template.json");

		for (FragmentEntryLinkModel fragmentEntryLinkModel :
				fragmentEntryLinkModels) {

			String itemId = SequentialUUID.generate();

			itemPageLayoutDefinitions.put(
				itemId,
				JSONFactoryUtil.createJSONObject(
					StringUtil.replace(
						StringUtil.replace(
							itemPageLayoutDefinitionTemplate, "${itemId}",
							itemId),
						"${fragmentEntryLinkId}",
						String.valueOf(
							fragmentEntryLinkModel.getFragmentEntryLinkId()))
				).toString());
		}

		String rootItemId = SequentialUUID.generate();

		JSONObject itemsJSONObject = JSONUtil.put(
			rootItemId,
			JSONUtil.put(
				"children",
				JSONFactoryUtil.createJSONArray(
					itemPageLayoutDefinitions.keySet())
			).put(
				"config", JSONFactoryUtil.createJSONObject()
			).put(
				"itemId", rootItemId
			).put(
				"parentId", StringPool.BLANK
			).put(
				"type", "root"
			));

		for (Map.Entry<String, String> entry :
				itemPageLayoutDefinitions.entrySet()) {

			itemsJSONObject.put(
				entry.getKey(),
				JSONFactoryUtil.createJSONObject(entry.getValue()));
		}

		return JSONUtil.put(
			"deletedItems", JSONFactoryUtil.createJSONArray()
		).put(
			"items", itemsJSONObject
		).put(
			"pageRules", JSONFactoryUtil.createJSONArray()
		).put(
			"rootItems",
			JSONUtil.put(
				"dropZone", ""
			).put(
				"main", rootItemId
			)
		).put(
			"version", 1.0
		).toString();
	}

	private String _generateData(
			List<FragmentEntryLinkModel> fragmentEntryLinkModels,
			String templateFileName)
		throws Exception {

		String data = _readFile("home_page_template/" + templateFileName);

		for (FragmentEntryLinkModel fragmentEntryLinkModel :
				fragmentEntryLinkModels) {

			String rendererKey = fragmentEntryLinkModel.getRendererKey();

			if (rendererKey.equals(_FRAGMENT_COMPONENT_RENDER_KEY_PARAGRAPH)) {
				int position = fragmentEntryLinkModel.getPosition();

				if (position == 0) {
					data = StringUtil.replace(
						data, "${paragraphTitleFragmentEntryLinkId}",
						String.valueOf(
							fragmentEntryLinkModel.getFragmentEntryLinkId()));
				}
				else {
					data = StringUtil.replace(
						data, "${paragraphContentFragmentEntryLinkId}",
						String.valueOf(
							fragmentEntryLinkModel.getFragmentEntryLinkId()));
				}
			}
			else {
				data = StringUtil.replace(
					data, "${imageFragmentEntryLinkId}",
					String.valueOf(
						fragmentEntryLinkModel.getFragmentEntryLinkId()));
			}
		}

		return data;
	}

	private InputStream _getFragmentComponentInputStream(
			String fragmentName, String suffix)
		throws Exception {

		return DataFactory.class.getResourceAsStream(
			StringBundler.concat(
				"/com/liferay/fragment/collection/contributor/basic/component",
				"/dependencies/", fragmentName, "/index.", suffix));
	}

	private String _getObjectLabel(String label) {
		return StringBundler.concat(
			"<?xml version=\"1.0\" ?><root available-locales=\"en_US\" ",
			"default-locale=\"en_US\"><Label language-id=\"en_US\">", label,
			"</Label></root>");
	}

	private String _getObjectPluralLabel(String label) {
		return StringBundler.concat(
			"<?xml version=\"1.0\"?><root available-locales=\"en_US\" ",
			"default-locale=\"en_US\"><PluralLabel language-id=\"en_US\">",
			label, "</PluralLabel></root>");
	}

	private String _getResourcePermissionModelName(String... classNames) {
		if (ArrayUtil.isEmpty(classNames)) {
			return StringPool.BLANK;
		}

		Arrays.sort(classNames);

		StringBundler sb = new StringBundler(classNames.length * 2);

		for (String className : classNames) {
			sb.append(className);
			sb.append(StringPool.DASH);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private CompanyModel _newCompanyModel(String webId) {
		CompanyModel companyModel = new CompanyModelImpl();

		// PK fields

		companyModel.setCompanyId(_counter.get());

		// Audit fields

		companyModel.setCreateDate(new Date());
		companyModel.setModifiedDate(new Date());

		// Other fields

		companyModel.setWebId(webId);
		companyModel.setMx("liferay.com");
		companyModel.setActive(true);
		companyModel.setName(webId);
		companyModel.setLegalName("Liferay, Inc.");

		return companyModel;
	}

	private LayoutModel _newContentPageLayoutModel(
		long groupId, long classNameId, long classPK, String name,
		String friendlyURL) {

		SimpleCounter simpleCounter = _layoutIdCounters.computeIfAbsent(
			LayoutLocalServiceImpl.getCounterName(groupId, false),
			counterName -> new SimpleCounter());

		LayoutModel layoutModel = new LayoutModelImpl();

		// PK fields

		layoutModel.setPlid(_layoutPlidCounter.get());

		// Group instance

		layoutModel.setGroupId(groupId);

		// Audit fields

		layoutModel.setCompanyId(_companyId);
		layoutModel.setUserId(_sampleUserId);
		layoutModel.setUserName(_SAMPLE_USER_NAME);
		layoutModel.setCreateDate(new Date());
		layoutModel.setModifiedDate(new Date());

		// Other fields

		layoutModel.setLayoutId(simpleCounter.get());
		layoutModel.setClassNameId(classNameId);
		layoutModel.setClassPK(classPK);
		layoutModel.setName(
			"<?xml version=\"1.0\"?><root><name>" + name + "</name></root>");
		layoutModel.setType(LayoutConstants.TYPE_CONTENT);

		int priority = 0;

		if (classNameId != 0) {
			layoutModel.setTypeSettings(
				StringUtil.replace(
					UnicodePropertiesBuilder.create(
						true
					).put(
						"published", "true"
					).buildString(),
					'\n', "\\n"));

			layoutModel.setHidden(true);
			layoutModel.setSystem(true);

			priority = Integer.MAX_VALUE;
		}

		layoutModel.setFriendlyURL(StringPool.FORWARD_SLASH + friendlyURL);
		layoutModel.setPriority(priority);
		layoutModel.setPublishDate(new Date());
		layoutModel.setStatusByUserId(_sampleUserId);
		layoutModel.setStatusByUserName(_SAMPLE_USER_NAME);
		layoutModel.setStatusDate(new Date());

		// Autogenerated fields

		String uuid = SequentialUUID.generate();

		layoutModel.setUuid(uuid);
		layoutModel.setExternalReferenceCode(uuid);

		return layoutModel;
	}

	private CounterModel _newCounterModel(String name, long currentId) {
		CounterModel counterModel = new CounterModelImpl();

		counterModel.setName(name);
		counterModel.setCurrentId(currentId);

		return counterModel;
	}

	private String _readFile(InputStream inputStream) throws Exception {
		List<String> lines = new ArrayList<>();

		StringUtil.readLines(inputStream, lines);

		return StringUtil.merge(lines, StringPool.SPACE);
	}

	private String _readFile(String resourceName) throws Exception {
		return _readFile(getResourceInputStream(resourceName));
	}

	private List<String> _readLines(InputStream inputStream) throws Exception {
		List<String> lines = new ArrayList<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new InputStreamReader(inputStream))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				lines.add(line);
			}
		}

		return lines;
	}

	private List<String> _readLines(String resourceName) throws Exception {
		return _readLines(getResourceInputStream(resourceName));
	}

	private static final long _CURRENT_TIME = System.currentTimeMillis();

	private static final long _DEFAULT_DL_FILE_ENTRY_TYPE_ID =
		DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT;

	private static final String _DEPENDENCIES_DIR =
		"/com/liferay/portal/tools/sample/sql/builder/dependencies/data/";

	private static final String _FRAGMENT_COMPONENT_RENDER_KEY_HEADING =
		"BASIC_COMPONENT-heading";

	private static final String _FRAGMENT_COMPONENT_RENDER_KEY_IMAGE =
		"BASIC_COMPONENT-image";

	private static final String _FRAGMENT_COMPONENT_RENDER_KEY_PARAGRAPH =
		"BASIC_COMPONENT-paragraph";

	private static final long _FUTURE_TIME =
		System.currentTimeMillis() + Time.YEAR;

	private static final String _JOURNAL_STRUCTURE_KEY = "BASIC-WEB-CONTENT";

	private static final String _SAMPLE_USER_NAME = "Sample";

	private static final Log _log = LogFactoryUtil.getLog(DataFactory.class);

	private static final PortletPreferencesFactory _portletPreferencesFactory =
		new PortletPreferencesFactoryImpl();

	private RoleModel _administratorRoleModel;
	private Map<Long, SimpleCounter>[] _assetCategoryCounters;
	private final Map<Long, List<AssetCategoryModel>>[]
		_assetCategoryModelsMaps =
			(Map<Long, List<AssetCategoryModel>>[])new HashMap<?, ?>
				[(BenchmarksPropsValues.MAX_COMPANY_COUNT + 1) *
					BenchmarksPropsValues.MAX_GROUP_COUNT];
	private final long[] _assetClassNameIds;
	private final Map<Long, Integer> _assetClassNameIdsIndexes =
		new HashMap<>();
	private final Map<Long, Integer> _assetPublisherQueryStartIndexes =
		new HashMap<>();
	private Map<Long, SimpleCounter>[] _assetTagCounters;
	private final Map<Long, List<AssetTagModel>>[] _assetTagModelsMaps =
		(Map<Long, List<AssetTagModel>>[])new HashMap<?, ?>
			[(BenchmarksPropsValues.MAX_COMPANY_COUNT + 1) *
				BenchmarksPropsValues.MAX_GROUP_COUNT];
	private final Map<String, ClassNameModel> _classNameModels =
		new HashMap<>();
	private long _companyId;
	private final SimpleCounter _counter;
	private final Map<Long, CPInstanceModel> _cpInstanceModels =
		new HashMap<>();
	private final PortletPreferencesImpl
		_defaultAssetPublisherPortletPreferencesImpl;
	private long _defaultDLDDMStructureId;
	private long _defaultDLDDMStructureVersionId;
	private long _defaultJournalDDMStructureId;
	private long _defaultJournalDDMStructureVersionId;
	private long _defaultJournalDDMTemplateId;
	private String _defaultListTypeEntryKey;
	private final String _dlDDMStructureContent;
	private final String _dlDDMStructureLayoutContent;
	private final SimpleCounter _dlFileEntryIdCounter;
	private AddressModel _firstAddressModel;
	private final List<String> _firstNames;
	private final FriendlyURLNormalizer _friendlyURLNormalizer;
	private final SimpleCounter _futureDateCounter;
	private long _globalGroupId;
	private final SimpleCounter _groupCounter;
	private long _guestGroupId;
	private RoleModel _guestRoleModel;
	private long _guestUserId;
	private AssetEntryModel _journalArticleAssetEntryModel;
	private String _journalArticleContent;
	private final Map<Long, String> _journalArticleResourceUUIDs =
		new HashMap<>();
	private final String _journalDDMStructureContent;
	private final String _journalDDMStructureLayoutContent;
	private final List<String> _lastNames;
	private final Map<String, SimpleCounter> _layoutIdCounters =
		new HashMap<>();
	private final String _layoutPageTemplateStructureRelData;
	private final SimpleCounter _layoutPlidCounter;
	private final SimpleCounter _layoutSetIdCounter;
	private final Set<String> _objectDefinitionDBTableNames = new HashSet<>();
	private long _objectDefinitionId;
	private long _objectFieldId;
	private RoleModel _ownerRoleModel;
	private final SimpleCounter _portletPreferenceValueIdCounter;
	private RoleModel _powerUserRoleModel;
	private final SimpleCounter _resourcePermissionIdCounter;
	private long _sampleUserId;
	private final SimpleCounter _segmentsExperienceCounter;
	private final Format _simpleDateFormat;
	private RoleModel _siteMemberRoleModel;
	private final SimpleCounter _socialActivityIdCounter;
	private final SimpleCounter _timeCounter;
	private final Map<Integer, Map<Long, String>> _treePathsMap =
		new HashMap<>();
	private RoleModel _userRoleModel;
	private final SimpleCounter _userScreenNameCounter;
	private String _webId;

}