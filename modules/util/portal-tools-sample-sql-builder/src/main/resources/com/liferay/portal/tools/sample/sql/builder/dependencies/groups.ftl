<#include "sample_user.ftl">

<#include "default_service_account_user.ftl">

<#include "commerce_groups.ftl">

<#include "asset.ftl">

<#include "ddm.ftl">

<#list dataFactory.newGroupModels() as groupModel>
	<#assign groupId = groupModel.groupId />

	<#include "asset_publisher.ftl">

	<#include "blogs.ftl">

	<#include "ddl.ftl">

	<#include "journal_article.ftl">

	<#include "fragment.ftl">

	<#include "mb.ftl">

	<#include "object_definition_layout.ftl">

	<#include "users.ftl">

	<@insertDLFolder
		_ddmStructureId = dataFactory.defaultDLDDMStructureId
		_dlFolderDepth = 1
		_groupModel = groupModel
		_parentDLFolderId = 0
	/>

	<#assign
		homePageContentLayoutModels = dataFactory.newContentPageLayoutModels(groupId, "home")
		homePageSegmentsExperienceModels = dataFactory.newSegmentsExperienceModels(homePageContentLayoutModels)
	 />

	 <#list homePageSegmentsExperienceModels as homePageSegmentsExperienceModel>
	 	${dataFactory.toInsertSQL(homePageSegmentsExperienceModel)}
	 </#list>

	<@insertContentPageLayout
		_fragmentEntryLinkModels = dataFactory.newFragmentEntryLinkModels(homePageContentLayoutModels, homePageSegmentsExperienceModels)
		_layoutModels = homePageContentLayoutModels
		_templateFileName = "default-homepage-layout-definition.json"
	/>

	<#list dataFactory.newGroupLayoutModels(groupId) as groupLayoutModel>
		<@insertLayout _layoutModel = groupLayoutModel />
	</#list>

	<@insertGroup _groupModel = groupModel />

	${csvFileWriter.write("repository", virtualHostModel.hostname + "," + groupModel.friendlyURL + "," + groupId + ", " + groupModel.name + "\n")}
</#list>

<#assign
	defaultSiteHomePageContentLayoutModels = dataFactory.newContentPageLayoutModels(guestGroupModel.groupId, "home")
	defaultSiteHomePageSegmentsExperienceModels = dataFactory.newSegmentsExperienceModels(defaultSiteHomePageContentLayoutModels)
/>
<#list defaultSiteHomePageSegmentsExperienceModels as defaultSiteHomePageSegmentsExperienceModel>
	${dataFactory.toInsertSQL(defaultSiteHomePageSegmentsExperienceModel)}
</#list>

<@insertContentPageLayout
	_fragmentEntryLinkModels = dataFactory.newFragmentEntryLinkModels(defaultSiteHomePageContentLayoutModels, defaultSiteHomePageSegmentsExperienceModels)
	_layoutModels = defaultSiteHomePageContentLayoutModels
	_templateFileName = "default-homepage-layout-definition.json"
/>

<#include "segments.ftl">

<#assign
	searchLayoutModel = dataFactory.newSearchLayoutModel(guestGroupModel.groupId, true)
	layoutPrototypeModel = dataFactory.newLayoutPrototypeModel(defaultAdminUserModel.userId)
	searchTemplateGroupModel = dataFactory.newSearchTemplateGroupModel(layoutPrototypeModel.layoutPrototypeId, defaultAdminUserModel.userId)
	searchGroupLayoutModel = dataFactory.newSearchGroupLayoutModel(searchTemplateGroupModel.groupId, searchLayoutModel)
/>

<@insertLayout _layoutModel = searchLayoutModel />

<@insertLayout _layoutModel = searchGroupLayoutModel />

${dataFactory.toInsertSQL(layoutPrototypeModel)}

<@insertGroup _groupModel = searchTemplateGroupModel />