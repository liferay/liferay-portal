<#if dataFactory.maxContentLayoutCount != 0>
	<#assign
		journalArticleResourceModel = dataFactory.newJournalArticleResourceModel(groupId)

		journalArticleModel = dataFactory.newJournalArticleModel(journalArticleResourceModel, 0, 1)
	/>

	${dataFactory.toInsertSQL(journalArticleResourceModel)}

	<@insertJournalArticle
		_insertAssetEntry = true
		_journalArticleModel = journalArticleModel
		_journalDDMStructureModel = defaultJournalDDMStructureModel
		_journalDDMTemplateModel = defaultJournalDDMTemplateModel
	/>

	<#if dataFactory.maxFragmentEntryLinkCount == 0>
		<#assign fragmentCollectionModel = dataFactory.newFragmentCollectionModel(groupId) />

		${dataFactory.toInsertSQL(fragmentCollectionModel)}

		<#assign fragmentEntryModel = dataFactory.newFragmentEntryModel(groupId, fragmentCollectionModel) />

		${dataFactory.toInsertSQL(fragmentEntryModel)}

		<#list dataFactory.newContentLayoutModels(groupId) as contentLayoutModel>
			<@insertContentLayout
				_fragmentEntryModel = fragmentEntryModel
				_journalArticleModel = journalArticleModel
				_layoutModel = contentLayoutModel
			/>

			${csvFileWriter.write("fragment", virtualHostModel.hostname + "," + groupModel.friendlyURL + "," + contentLayoutModel.friendlyURL + "\n")}
		</#list>
	</#if>

	<#if dataFactory.maxFragmentEntryLinkCount != 0>
		<#list dataFactory.getSequence(dataFactory.maxContentLayoutCount) as contentLayoutCount>
			<#assign
				contentLayoutModels = dataFactory.newContentPageLayoutModels(groupId, groupId + "_web_content_" + contentLayoutCount)

				segmentsExperienceModels = dataFactory.newSegmentsExperienceModels(contentLayoutModels)
			/>

			<#list segmentsExperienceModels as segmentsExperienceModel>
				${dataFactory.toInsertSQL(segmentsExperienceModel)}
			</#list>

			<#list contentLayoutModels as contentLayoutModel>
				<#assign
					fragmentEntryLinkModels = dataFactory.newFragmentEntryLinkModels(journalArticleModel, contentLayoutModel, segmentsExperienceModels)

					layoutPageTemplateStructureModel = dataFactory.newLayoutPageTemplateStructureModel(contentLayoutModel)
				/>

				<#list fragmentEntryLinkModels as fragmentEntryLinkModel>
					${dataFactory.toInsertSQL(fragmentEntryLinkModel)}

					${dataFactory.toInsertSQL(dataFactory.newLayoutClassedModelUsageModel(groupId, contentLayoutModel.plid, "${fragmentEntryLinkModel.fragmentEntryLinkId}", journalArticleResourceModel))}
				</#list>

				${dataFactory.toInsertSQL(dataFactory.newLayoutFriendlyURLModel(contentLayoutModel))}

				${dataFactory.toInsertSQL(contentLayoutModel)}

				${dataFactory.toInsertSQL(layoutPageTemplateStructureModel)}

				${dataFactory.toInsertSQL(dataFactory.newLayoutPageTemplateStructureRelModel(contentLayoutModel, layoutPageTemplateStructureModel, fragmentEntryLinkModels))}

				 <#if contentLayoutModel.friendlyURL?contains("_web_content_")>
					${csvFileWriter.write("fragment", virtualHostModel.hostname + "," + groupModel.friendlyURL + "," + contentLayoutModel.friendlyURL + "\n")}
				</#if>
			</#list>
		</#list>
	</#if>
</#if>