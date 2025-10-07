<#if (ObjectEntry_objectEntryId.getData())?? && ObjectEntry_objectEntryId.getData()?has_content>
	<#assign assetId = ObjectEntry_objectEntryId.getData()?number />
</#if>

<#if ObjectEntry_status.getData()?? && ObjectEntry_status.getData() == 'approved'>
	<#if stringUtil.equals(locale, "en_US")>
		<#assign createDate = ObjectEntry_createDate.getData()?datetime("M/d/yy h:mm a") />
	<#else>
		<#assign createDate = ObjectEntry_createDate.getData()?datetime("yy/MM/dd HH:mm") />
	</#if>

	<div class="learn-knowledge-article-container">
		<div class="d-flex header-navigation justify-content-between mb-3 px-3">
			<div class="d-flex justify-content-between">
				<ul aria-label="breadcrumb navigation" class="learn-breadcrumb" role="navigation">
					<li>
						<a href="/">
							<@clay["icon"] symbol="home-full" />
						</a>
					</li>
					<li>
						${ObjectField_title.getData()}
					</li>
				</ul>
			</div>

			<div class="component-button learn-submit-feedback text-break">
				<a
					class="btn btn-link btn-nm page-editor__editable" data-lfr-editable-id="link" data-lfr-editable-type="link"
					data-tooltip-floating="true"
					href="https://liferay.dev/c/portal/login?redirect=https://liferay.dev/ask/questions/liferay-learn-feedback/new"
					id="fragment-txnc-link">
						<@liferay_ui["message"] key="submit-feedback" />
				</a>
			</div>
		</div>

		<div class="d-flex knowledge-article-page-container">
			<div class="container knowledge-article-main" id="left-panel">
				<div class="disclaimers-container">
					<div class="disclaimer">
						<div class="container-fluid">
							<div class="d-flex disclaimer-header">
								<div class="col knowledge-article-dialect">
									<#if getterUtil.getBoolean(ObjectField_legacy.getData())>
										<@liferay_ui["message"] key="legacy-knowledge-base" />
									<#else>
										<@liferay_ui["message"] key="knowledge-base" />
									</#if>
								</div>

								<span>
									<@liferay_ui["message"] key="published" />
									${createDate?string("MMM. d, yyyy")}
								</span>
							</div>

							<#if (ObjectField_title.getData())??>
								<h3 class="disclaimer-title mb-3">
									${ObjectField_title.getData()}
								</h3>
							</#if>

							<div class="d-flex description-container">
								<div class="author-container d-flex">
									<img class="publisher-avatar rounded-circle" src="${ObjectEntry_userProfileImage.getData()}" />

									<div class="col-1.5 mx-3">
										<@liferay_ui["message"] key="written-by" />

										<p class="author">
											<#if (ObjectField_authorName.getData())??>
												${ObjectField_authorName.getData()}
											</#if>
										</p>
									</div>
								</div>

								<div class="col paragraph">
									<p class="disclaimer-how-to d-none">
										<@liferay_ui["message"] key="knowledge-article-header-disclaimer-how-to" />
									</p>

									<p class="disclaimer-default d-none">
										<@liferay_ui["message"] key="knowledge-article-header-disclaimer" />
									</p>
								</div>
							</div>
						</div>
					</div>

					<#if getterUtil.getBoolean(ObjectField_legacy.getData())>
						<div class="d-flex warning-disclaimer">
							<div>
								<span class="icon-warning"></span>
							</div>

							<div class="col">
								<p>
									<@liferay_ui["message"] key="legacy-article" />
								</p>

								<@liferay_ui["message"] key="learn-legacy-article-disclaimer-text" />
							</div>
						</div>
					</#if>
				</div>

				<article class="knowledge-article-content">
					<#if (ObjectField_content.getData())??>
						${ObjectField_content.getData()}
					</#if>
				</article>

				<div class="knowledge-article-attachments-container">
				</div>

				<div class="align-items-center bold d-flex flex-row p-3 rating-box">
					<div>
						<@liferay_ui["message"] key="did-this-article-resolve-your-issue" />
					</div>

					<div class="<#if themeDisplay.isSignedIn()>enabled<#else>disabled</#if>">
						<@liferay_ratings["ratings"]
							className="com.liferay.journal.model.JournalArticle"
							classPK=assetId
							type="thumbs"
						/>
					</div>
				</div>

				<div class="learn-category-section-bottom">
					<hr class="my-5" />
				</div>
			</div>

			<div class="col-md-3 knowledge-article-sidemenu" id="right-panel">
				<div class="sticky-panel">
					<div class="container-fluid">
						<div class="row sidemenu-header">
							<div class="col knowledge-article-dialect">
								<#if getterUtil.getBoolean(ObjectField_legacy.getData())>
									<@liferay_ui["message"] key="legacy-knowledge-base" />
								<#else>
									<@liferay_ui["message"] key="knowledge-base" />
								</#if>
							</div>
						</div>
					</div>

					<div class="article-nav-menu page-nav-menu">
					</div>

					<div class="page-nav-menu">
						<div class="category-container mb-3">
							<div class="learn-category-section-side">
							</div>
						</div>

						<hr />

						<div class="align-items-center d-flex flex-row rating-box">
							<div>
								<@liferay_ui["message"] key="did-this-article-resolve-your-issue" />
							</div>

							<@liferay_ratings["ratings"]
								className="com.liferay.journal.model.JournalArticle"
								classPK=assetId
								type="thumbs"
							/>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
<#else>
	<meta content="0; URL='/not-found'" http-equiv="refresh" />
</#if>

<script>
	async function main() {
		window.addEventListener("load", function() {
			document.querySelectorAll(".btn-thumbs-down").forEach((element) => {
				element.addEventListener("click", (event) => {
					location.reload();
				});
			});

			document.querySelectorAll(".btn-thumbs-up").forEach((element) => {
				element.addEventListener("click", (event) => {
					location.reload();
				});
			});
		});

		async function fetchKnowledgeArticle() {
			try {
				const response = await Liferay.Util.fetch(`/o/c/p2s3knowledgearticles/${assetId}?nestedFields=embeddedTaxonomyCategory,p2s3KnowledgeArticleToP2S3Attachments`);

				if (!response.ok) {
					console.error('Request error:', response.statusText);

					return;
				}

				return response.json();
			}
			catch (error) {
				console.error('Error fetching data:', error);
			}
		}

		function createNavMenuContainer() {
			if (!knowledgeArticle?.content) {
				return;
			}

			const pageNavMenu = document.querySelector('.page-nav-menu');

			while (pageNavMenu.firstChild) {
				pageNavMenu.removeChild(pageNavMenu.firstChild);
			}

			const knowledgeArticleContent = document.querySelector('.knowledge-article-content');

			knowledgeArticleContent.innerHTML = knowledgeArticle.content;

			const documentFragment = document.createDocumentFragment();

			const h2Elements = knowledgeArticleContent.querySelectorAll('h2');

			h2Elements.forEach((h2Element) => {
				const textContent = h2Element.textContent.trim();

				let id = h2Element.getAttribute('id');

				if (!id) {
					id = textContent.toLowerCase().replace(/\s+/g, '').replace(/[^\w\-] /g, '');
					h2Element.setAttribute('id', id);
				}

				const anchorElement = document.createElement('a');

				anchorElement.className = 'btn btn-nm btn-link px-0';
				anchorElement.href = "#" + id;
				anchorElement.textContent = textContent;

				const divElement = document.createElement('div');

				divElement.className = 'component-button text-break';
				divElement.appendChild(anchorElement);

				documentFragment.appendChild(divElement);

			});

			pageNavMenu.appendChild(documentFragment);
		}

		async function createAttachmentsContainter() {
			const knowledgeArticleAttachments = knowledgeArticle?.p2s3KnowledgeArticleToP2S3Attachments;

			if(!knowledgeArticleAttachments?.length) {
				return;
			}

			const knowledgeArticleAttachmentsContainer = document.querySelector('.knowledge-article-attachments-container');

			if (!knowledgeArticleAttachmentsContainer) {
				return;
			}

			const documentFragment = document.createDocumentFragment();

			knowledgeArticleAttachments.forEach((item) => {
				const knowledgeArticleAttachmentTitleElement = document.createElement('p');

				knowledgeArticleAttachmentTitleElement.textContent = item.name;

				const downloadButtonElement = document.createElement('a');

				downloadButtonElement.className = 'download-button';
				downloadButtonElement.download = item.file?.name || 'file';
				downloadButtonElement.href = item.file?.link?.href || '#';
				downloadButtonElement.target = '_blank';
				downloadButtonElement.textContent = 'Download';

				const knowledgeArticleAttachmentCardElement = document.createElement('div');

				knowledgeArticleAttachmentCardElement.appendChild(knowledgeArticleAttachmentTitleElement);
				knowledgeArticleAttachmentCardElement.appendChild(downloadButtonElement);
				knowledgeArticleAttachmentCardElement.className = 'attachment-card';

				documentFragment.appendChild(knowledgeArticleAttachmentCardElement);
			});

			knowledgeArticleAttachmentsContainer.appendChild(documentFragment);
		}

		async function createTaxonomyCategoriesContainer() {
			const taxonomyCategoryBriefs = knowledgeArticle?.taxonomyCategoryBriefs;

			if (!taxonomyCategoryBriefs?.length) {
				return;
			}

			const taxonomyCategoriesByTaxonomyVocabularyMap = new Map();

			for (const taxonomyCategoryBrief of taxonomyCategoryBriefs) {
				const {embeddedTaxonomyCategory, taxonomyCategoryId, taxonomyCategoryName} = taxonomyCategoryBrief;

				const {parentTaxonomyVocabulary} = embeddedTaxonomyCategory;

				const {externalReferenceCode: taxonomyVocabularyExternalReferenceCode, name: taxonomyVocabularyName} = parentTaxonomyVocabulary;

				let taxonomyVocabulary = taxonomyCategoriesByTaxonomyVocabularyMap.get(taxonomyVocabularyName);

				if (!taxonomyVocabulary) {
					taxonomyVocabulary = {
						externalReferenceCode: taxonomyVocabularyExternalReferenceCode,
						key:taxonomyVocabularyName.toLowerCase(),
						name: taxonomyVocabularyName,
						taxonomyCategories: []
					};

					taxonomyCategoriesByTaxonomyVocabularyMap.set(taxonomyVocabularyName, taxonomyVocabulary);
				}

				taxonomyVocabulary.taxonomyCategories.push({
			  		id: taxonomyCategoryId,
			  		name: taxonomyCategoryName,
					vocabulary: taxonomyVocabularyName.toLowerCase(),
				});
			}

			const taxonomyCategoriesByTaxonomyVocabulary = Array.from(taxonomyCategoriesByTaxonomyVocabularyMap.values());

			if(!taxonomyCategoriesByTaxonomyVocabulary?.length) {
				return;
			}

			const bottomTaxonomyCategoriesExternalReferenceCode = ['CAPABILITY', 'FEATURE'];
			const sideTaxonomyCategoriesExternalReferenceCode = ['APPLICABLE-VERSIONS', 'DEPLOYMENT-APPROACH'];

			taxonomyCategoriesByTaxonomyVocabulary.forEach((item) => {
				const taxonomyVocabularyTitleElement = document.createElement('h6');

				taxonomyVocabularyTitleElement.textContent = item.name;

				const sectionElement = document.createElement('section');

				sectionElement.appendChild(taxonomyVocabularyTitleElement);

				const categoryTagsElement = document.createElement('div');

				categoryTagsElement.className = 'category-tags';

				let spanClassName = 'category-tag-side';
				let targetContainer = document.querySelector('.learn-category-section-side');

				if(bottomTaxonomyCategoriesExternalReferenceCode.includes(item.externalReferenceCode)) {
					spanClassName = 'category-tag-bottom';
					targetContainer = document.querySelector('.learn-category-section-bottom');
				}

				item.taxonomyCategories.forEach(taxonomyCategory => {
					const linkElement = document.createElement('a');

					if (taxonomyCategory && spanClassName === 'category-tag-bottom') {
						linkElement.href = '/search?' + taxonomyCategory.vocabulary + '=' + taxonomyCategory.id ;
					}

					linkElement.className = spanClassName;
		  			linkElement.textContent = taxonomyCategory.name;

					categoryTagsElement.appendChild(linkElement);
				});

				sectionElement.appendChild(categoryTagsElement);

				if(targetContainer) {
					targetContainer.appendChild(sectionElement);
				}
			});
		}

		async function createDisclaimerMessage() {
			const defaultDisclaimer = document.querySelector('.disclaimer-default');
			const howToDisclaimer = document.querySelector('.disclaimer-how-to');

			if (knowledgeArticle?.knowledgeArticleType.key == 'howTo') {
				howToDisclaimer.classList.remove('d-none');
			}
			else {
				defaultDisclaimer.classList.remove('d-none');
			}
		}

		const knowledgeArticle = await fetchKnowledgeArticle();

		createAttachmentsContainter();
		createDisclaimerMessage();
		createNavMenuContainer();
		createTaxonomyCategoriesContainer();
	}

	main();
</script>

<style>
	.attachment-card {
		align-items: center;
		display: flex;
		border: 1px solid #E2E2E4;
		border-radius: 0.75rem;
		height: 5rem;
		justify-content: space-between;
		padding: 1rem;
		width: 32rem;

		p {
			align-items: center;
			display: inline-flex;
			font-weight: 600;
			margin: 0;

			&::before {
				background-image: url("data:image/svg+xml,%3Csvg width='32' height='32' viewBox='0 0 32 32' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cmask id='mask0_1031_5862' style='mask-type:alpha' maskUnits='userSpaceOnUse' x='4' y='1' width='24' height='30'%3E%3Cpath d='M20.5 1H7C5.3418 1 4 2.3418 4 4V28C4 29.6582 5.3418 31 7 31H25C26.6582 31 28 29.6582 28 28V8.5L20.5 1Z' fill='%236B6C7E'/%3E%3C/mask%3E%3Cg mask='url(%23mask0_1031_5862)'%3E%3Crect width='32' height='32' fill='%23377CFF'/%3E%3C/g%3E%3C/svg%3E");
				background-size: cover;
				content: "";
				display: inline-block;
				height: 32px;
				margin-right: 0.5rem;
				width: 32px;
			}
		}
	}

	.btn-thumbs-up,
	.btn-thumbs-down {
		height: 40px;
		padding: 0;
		width: 40px;
	}

	.category-tag-side {
		background-color: var(--color-state-info-lighten-2, #e6ebf5);
		border-radius: 4px;
		border-style: none;
		color: #1C3667;
		display: inline-block;
		font-size: 11px;
		font-weight: 400;
		padding: 2px 8px;
		white-space: nowrap;
	}

	.category-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 0.5rem;
		margin-bottom: 1rem;
	}

	.disclaimer {
		.author {
			font-weight: bold;
		}

		.knowledge-article-dialect {
			align-items: center;
			color: var(--color-brand-primary);
			margin-bottom: 1rem;
			padding: 0;

			&::before {
				background-image: url("data:image/svg+xml,%3Csvg width='16' height='16' viewBox='0 0 16 16' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='%230B5FFF'%3E%3Cpath d='M3 1H1C0.45 1 0 1.45 0 2V3H4V2C4 1.45 3.55 1 3 1Z'/%3E%3Cpath d='M0 14C0 14.55 0.45 15 1 15H3C3.55 15 4 14.55 4 14V13H0V14Z'/%3E%3Cpath d='M4 4H0V12H4V4Z'/%3E%3Cpath d='M8 1H6C5.45 1 5 1.45 5 2V3H9V2C9 1.45 8.55 1 8 1Z'/%3E%3Cpath d='M5 14C5 14.55 5.45 15 6 15H8C8.55 15 9 14.55 9 14V13H5V14Z'/%3E%3Cpath d='M9 4H5V12H9V4Z'/%3E%3Cpath d='M11.22 1.1L10.25 1.35C9.72 1.49 9.4 2.04 9.54 2.57L9.79 3.54L12.69 2.78L12.44 1.82C12.3 1.28 11.76 0.96 11.22 1.1Z'/%3E%3Cpath d='M12.95 3.75L10.04 4.51L12.06 12.25L14.96 11.49L12.95 3.75Z'/%3E%3Cpath d='M12.56 14.18C12.7 14.72 13.24 15.04 13.78 14.9L14.75 14.65C15.28 14.51 15.6 13.96 15.46 13.43L15.21 12.46L12.31 13.22L12.56 14.18Z'/%3E%3C/g%3E%3C/svg%3E");
			}
		}

		.paragraph {
			font-style: italic;
		}
	}

	.download-button {
		align-items: center;
		border: 1px solid black;
		border-radius: 0.5rem;
		color: black;
		display: inline-flex;
		height: 2rem;
		padding: 0.5rem 0.75rem;

		&::after {
			background-image: url("data:image/svg+xml,%3Csvg%20width%3D%2217%22%20height%3D%2216%22%20viewBox%3D%220%200%2017%2016%22%20fill%3D%22none%22%20xmlns%3D%22http%3A//www.w3.org/2000/svg%22%3E%3Cmask%20id%3D%22mask0_1031_5875%22%20style%3D%22mask-type%3Aalpha%22%20maskUnits%3D%22userSpaceOnUse%22%20x%3D%222%22%20y%3D%220%22%20width%3D%2213%22%20height%3D%2216%22%3E%3Cpath%20d%3D%22M8.5%2012C8.23145%2012%207.97461%2011.8937%207.78418%2011.7031L5.54688%209.46564C5.14941%209.0719%205.14941%208.43127%205.54688%208.03436C5.94043%207.63751%206.58105%207.63751%206.97754%208.03436L7.5%208.55627V1C7.5%200.446899%207.94629%200%208.5%200C9.05273%200%209.5%200.446899%209.5%201V8.55627L10.0215%208.03436C10.2188%207.83752%2010.4775%207.73749%2010.7373%207.73749C10.9971%207.73749%2011.2559%207.83752%2011.4531%208.03436C11.8496%208.4281%2011.8496%209.06873%2011.4531%209.46564L9.21582%2011.7031C9.02441%2011.8937%208.76855%2012%208.5%2012Z%22%20fill%3D%22%236B6C7E%22/%3E%3Cpath%20d%3D%22M12.5%2012C12.5%2011.4469%2012.9473%2011%2013.5%2011C14.0527%2011%2014.5%2011.4469%2014.5%2012V14C14.5%2015.1046%2013.6046%2016%2012.5%2016H4.5C3.39543%2016%202.5%2015.1046%202.5%2014V12C2.5%2011.4469%202.94727%2011%203.5%2011C4.05273%2011%204.5%2011.4469%204.5%2012V14H12.5V12Z%22%20fill%3D%22%236B6C7E%22/%3E%3C/mask%3E%3Cg%20mask%3D%22url(%23mask0_1031_5875)%22%3E%3Crect%20x%3D%220.5%22%20width%3D%2216%22%20height%3D%2216%22%20fill%3D%22%232B3A4B%22/%3E%3C/g%3E%3C/svg%3E");
			background-repeat: no-repeat;
			background-size: contain;
			content: '';
			display: inline-block;
			height: 16px;
			margin;
			width: 17px;
		}
	}

	.learn-category-section-bottom {
		.category-tag-bottom {
			background: var(--color-neutral-0, #fff);
			border: 1px solid var(--color-brand-primary, #0b5fff);
			border-radius: var(--border-radius-xl);
			color: var(--color-brand-primary, #0b5fff);
			padding: 0 0.75rem;
			height: 24px;
		}

		section {
			align-items: baseline;
			display: flex;
			gap: 1rem;
		}
	}

	.knowledge-article-attachments-container {
		display: flex;
		flex-direction: column;
		gap: 1.5rem;
		margin-top: 1rem;
	}

	.knowledge-article-dialect {
		display: flex;
		font-family: var(--font-family-sans-serif);
		font-size: 18px;
		font-weight: 600;

		&::before {
			background-repeat: no-repeat;
			background-size: contain;
			content: "";
			display: inline-block;
			height: 16px;
			margin-right: 0.5rem;
			width: 16px;
		}
	}

	.knowledge-article-main {
		display: flex;
		flex-direction: column;

		.rating-box {
			background: linear-gradient(123.06deg, #1514A4 0%, #00C2FB 173.41%);
			border-radius: 10px;
			color: var(--color-neutral-0, #fff);
			margin-top: 2rem;
			width: max-content;

			.enabled .lexicon-icon,
			.enabled span {
				fill: white;
				color: white;
			}

			.disabled .lexicon-icon,
			.disabled span {
				fill: #6e9dde;
				color: #6e9dde;
			}
		}
	}

	.learn-breadcrumb {
		align-items: center;
		display: flex;
		flex-wrap: wrap;
		list-style: none;
		margin: 0;
		padding: 0;

		a,
		li {
			color: var(--color-state-neutral-darken-1, #6c6c75);
			font-size: 0.8125rem;
			text-decoration: none;
		}

		li+li::before {
			content: '/';
			padding: 0 4px;
		}
	}

	.learn-submit-feedback {
		.btn-link::after {
			content: url("data:image/svg+xml,%3Csvg%20width%3D%2216%22%20height%3D%2216%22%20viewBox%3D%220%200%2016%2016%22%20fill%3D%22none%22%20xmlns%3D%22http%3A//www.w3.org/2000/svg%22%3E%3Cmask%20id%3D%22mask0_1005_5813%22%20style%3D%22mask-type%3Aalpha%22%20maskUnits%3D%22userSpaceOnUse%22%20x%3D%220%22%20y%3D%220%22%20width%3D%2216%22%20height%3D%2217%22%3E%3Cpath%20d%3D%22M14%202.00383H15C15.5498%202.00383%2015.9971%202.45384%2016%203.00383V10.0038C16%2010.5569%2015.5527%2011.0038%2015%2011.0038H13V5.96629C13%204.88194%2012.1221%204.00383%2011.0371%204.00383H4V3.00383C4%202.45067%204.44727%202.00383%205%202.00383H10.5938L12.2939%200.297593C13.0156%20-0.386795%2014.0098%200.219468%2014%201.00383V2.00383Z%22%20fill%3D%22%236B6C7E%22/%3E%3Cpath%20d%3D%22M3.52539%208.00383H8.52539C9.22461%208.00383%209.125%209.00383%208.52539%209.00383H3.52539C2.8252%209.00383%202.8252%208.00383%203.52539%208.00383Z%22%20fill%3D%22%236B6C7E%22/%3E%3Cpath%20d%3D%22M6.52539%2010.0038H3.52539C2.8252%2010.0038%202.8252%2011.0038%203.52539%2011.0038H6.52539C7.125%2011.0038%207.22461%2010.0038%206.52539%2010.0038Z%22%20fill%3D%22%236B6C7E%22/%3E%3Cpath%20fill-rule%3D%22evenodd%22%20clip-rule%3D%22evenodd%22%20d%3D%22M11%205.00383H1C0.447266%205.00383%200%205.45073%200%206.00383V13.0038C0%2013.5569%200.447266%2014.0038%201%2014.0038H2V15.0038C2.0127%2015.9476%203.13086%2016.3069%203.70605%2015.7101L5.40625%2014.0038H11C11.5527%2014.0038%2012%2013.5569%2012%2013.0038V6.00383C12%205.45073%2011.5498%205.00383%2011%205.00383ZM11%2013.0038H1V6.00383H11V13.0038Z%22%20fill%3D%22%236B6C7E%22/%3E%3C/mask%3E%3Cg%20mask%3D%22url(%23mask0_1005_5813)%22%3E%3Crect%20width%3D%2216%22%20height%3D%2216%22%20fill%3D%22%230B5FFF%22/%3E%3C/g%3E%3C/svg%3E");
			display: inline-block;
			margin-left: 5px;
		}

		a {
			color: var(--color-brand-primary, #0b5fff);
			font-size: 1rem;
			font-style: normal;
			font-weight: 600;
			line-height: 1.5rem;

			&:hover {
				color: var(--color-action-primary-active, #004ad7);
			}
		}
	}

	.publisher-avatar {
		height: 48px;
		width: 48px;
	}

	.ratings-thumbs {
		display: flex;
		margin-left: 1rem;
		max-width: 80px;
	}

	.sidemenu-header .knowledge-article-dialect {
		align-items: center;
		color: white;

		&::before {
			background-image: url("data:image/svg+xml,%3Csvg%20width%3D%2216%22%20height%3D%2216%22%20viewBox%3D%220%200%2016%2016%22%20fill%3D%22none%22%20xmlns%3D%22http%3A//www.w3.org/2000/svg%22%3E%3Cmask%20id%3D%22mask0_1005_5947%22%20style%3D%22mask-type%3Aalpha%22%20maskUnits%3D%22userSpaceOnUse%22%20x%3D%220%22%20y%3D%221%22%20width%3D%2216%22%20height%3D%2214%22%3E%3Cpath%20d%3D%22M3%201H1C0.446875%201%200%201.44687%200%202V3H4V2C4%201.44687%203.55312%201%203%201Z%22%20fill%3D%22%236B6C7E%22/%3E%3Cpath%20d%3D%22M0%2014C0%2014.5531%200.446875%2015%201%2015H3C3.55312%2015%204%2014.5531%204%2014V13H0V14Z%22%20fill%3D%22%236B6C7E%22/%3E%3Cpath%20d%3D%22M4%204H0V12H4V4Z%22%20fill%3D%22%236B6C7E%22/%3E%3Cpath%20d%3D%22M8.00001%201H6.00001C5.44688%201%205.00001%201.44687%205.00001%202V3H9.00001V2C9.00001%201.44687%208.55313%201%208.00001%201Z%22%20fill%3D%22%236B6C7E%22/%3E%3Cpath%20d%3D%22M5.00001%2014C5.00001%2014.5531%205.44688%2015%206.00001%2015H8.00001C8.55313%2015%209.00001%2014.5531%209.00001%2014V13H5.00001V14Z%22%20fill%3D%22%236B6C7E%22/%3E%3Cpath%20d%3D%22M9.00001%204H5.00001V12H9.00001V4Z%22%20fill%3D%22%236B6C7E%22/%3E%3Cpath%20d%3D%22M11.2212%201.10002L10.2525%201.35315C9.71809%201.49377%209.39621%202.03752%209.53684%202.5719L9.78996%203.54065L12.6931%202.7844L12.44%201.81565C12.2993%201.28127%2011.7556%200.959396%2011.2212%201.10002Z%22%20fill%3D%22%236B6C7E%22/%3E%3Cpath%20d%3D%22M12.9456%203.75117L10.0422%204.50657L12.0566%2012.249L14.96%2011.4936L12.9456%203.75117Z%22%20fill%3D%22%236B6C7E%22/%3E%3Cpath%20d%3D%22M12.5586%2014.1844C12.6993%2014.7188%2013.243%2015.0407%2013.7774%2014.9001L14.7461%2014.6469C15.2805%2014.5063%2015.6024%2013.9626%2015.4618%2013.4282L15.2086%2012.4594L12.3055%2013.2157L12.5586%2014.1844Z%22%20fill%3D%22%236B6C7E%22/%3E%3C/mask%3E%3Cg%20mask%3D%22url(%23mask0_1005_5947)%22%3E%3Crect%20width%3D%2216%22%20height%3D%2216%22%20fill%3D%22white%22/%3E%3C/g%3E%3C/svg%3E");
		}
	}

	.sticky-panel {
		position: sticky;
		top: 200px;
	}

	.warning-disclaimer {
		background-color: #FFF4EC;
		border-radius: var(--border-radius-lg);
		margin-bottom: var(--spacer-4, 1.5rem);
		margin-right: var(--spacer-3, 1rem);
		padding: var(--spacer-4, 1.5rem);

		.icon-warning::before {
			background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16'%3E%3Cpath d='M8.875 7.143c0 .838 0 1.17 0 1.714 0 .474-.392.857-.875.857s-.875-.383-.875-.857V7.143c0-.838 0-.527 0-1.072 0-.474.392-.857.875-.857s.875.383.875.857c0 .545 0 .234 0 1.072zM8 12.5c-.483 0-.875-.384-.875-.857 0-.474.392-.857.875-.857s.875.383.875.857c0 .473-.392.857-.875.857zm6.844-.394L8.992 2.632A.875.875 0 0 0 8 2c-.413 0-.784.236-.992.632L1.156 12.106a.875.875 0 0 0 .992 1.261h11.704a.875.875 0 0 0 .992-1.261z' fill='%23B95000'/%3E%3C/svg%3E");
			background-repeat: no-repeat;
			background-size: contain;
			content: "";
			display: inline-block;
			height: 16px;
			margin-right: 4px;
			vertical-align: middle;
			width: 16px;
		}

		p {
			color: #B95000;
			margin-bottom: 0;
			text-transform: uppercase;
		}
	}

	article h2 {
		scroll-margin-top: 11rem;
	}

	article h3 {
		scroll-margin-top: 11rem;
	}

	h6 {
		font-size: 1rem;
	}

	html {
		scroll-behavior: smooth;
	}

	@media (max-width: 600px) {
		.description-container,
		.knowledge-article-page-container {
			flex-direction: column;
		}

		.knowledge-article-sidemenu {
			margin: 2rem auto;
			max-width: var(--container-max-sm, 540px);
		}
	}

	@media (min-width: 600px) and (max-width: 700px) {
		.description-container {
			flex-direction: column;
		}

		.knowledge-article-sidemenu {
			margin: 0 auto;
			max-width: var(--container-max-sm, 540px);
		}
	}
</style>