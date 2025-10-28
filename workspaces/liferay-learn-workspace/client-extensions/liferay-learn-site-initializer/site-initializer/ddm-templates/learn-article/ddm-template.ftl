<script>
	const _addEventListener = (selectors) => {
		var elements = document.querySelectorAll(selectors);

		elements.forEach((element) => {
			element.addEventListener("click", (event) => {
				event.preventDefault();

				const anchorElement = document.getElementById(element.getAttribute("id").replace("toc-", ""));

				if (anchorElement) {
					window.history.pushState(
						{},
						"",
						"#" + element.getAttribute("id").replace("toc-", "")
					);
					scrollToElement(anchorElement);
				}
			});
		});
	}

	const scrollToElement = (element) => {
		if (!element) return;

		window.scrollTo({
			behavior: "smooth",
			top: element.getBoundingClientRect().top + window.scrollY - 190,
		});
	};

	window.addEventListener('load', function() {
		_addEventListener("h1 a, h2 a, h3 a");
		_addEventListener(".toc li a");

		if (window.location.hash) {
			const hashLocation = document.getElementById(window.location.hash.substring(1));

			if (hashLocation) {
				setTimeout(() => {
					scrollToElement(hashLocation);
				}, 100);
			}
		}
	});
</script>

<#assign
	journalArticleId = .vars["reserved-article-id"].data

	structuredContent = restClient.get("/headless-delivery/v1.0/sites/${groupId}/structured-contents/by-key/${journalArticleId}?nestedFields=embeddedTaxonomyCategory")

	showChildrenCards = showChildrenCards.getData()?boolean
	taxonomyCategoriesMap = {}
	taxonomyVocabularies = []
	taxonomyCategoryBriefs = structuredContent.taxonomyCategoryBriefs

	navigationJSONObject = jsonFactoryUtil.createJSONObject(htmlUtil.unescape(navigation.getData()?trim))

	breadcrumbJSONArray = navigationJSONObject.getJSONArray("breadcrumb")
	childrenJSONArray = navigationJSONObject.getJSONArray("children")
/>

<#list taxonomyCategoryBriefs as taxonomyCategoryBrief>
	<#assign taxonomyVocabularyName = taxonomyCategoryBrief.embeddedTaxonomyCategory.parentTaxonomyVocabulary.name />

	<#if !taxonomyVocabularies?seq_contains(taxonomyVocabularyName)>
		<#assign taxonomyVocabularies = taxonomyVocabularies + [taxonomyVocabularyName] />
	</#if>

	<#if taxonomyCategoriesMap[taxonomyVocabularyName]?has_content>
		<#assign taxonomyCategoriesMap = taxonomyCategoriesMap +
			{
				taxonomyVocabularyName:
					taxonomyCategoriesMap[taxonomyVocabularyName] + [{
						"categoryId": taxonomyCategoryBrief.taxonomyCategoryId,
						"categoryName": taxonomyCategoryBrief.taxonomyCategoryName
					}]
			}
		/>
	<#else>
		<#assign taxonomyCategoriesMap = taxonomyCategoriesMap +
			{
				taxonomyVocabularyName:
					[{
						"categoryId": taxonomyCategoryBrief.taxonomyCategoryId,
						"categoryName": taxonomyCategoryBrief.taxonomyCategoryName
					}]
			}
		/>
	</#if>
</#list>

<article class="learn-article">
	<div class="d-flex flex-column">
		<div class="learn-article-breadcrumbs">
			<div class="learn-article-breadcrumbs-content">
				<div class="align-items-baseline d-flex justify-content-between mb-3">
					<ul
						aria-label="breadcrumb navigation"
						class="learn-article-breadcrumb"
						role="navigation"
					>
						<li>
							<a href="/"><@clay["icon"] symbol="home-full" /></a>
						</li>

						<#if breadcrumbJSONArray?has_content>
							<#list breadcrumbJSONArray.length()-1..0 as i>
								<#assign breadcrumbJSONObject = breadcrumbJSONArray.getJSONObject(i) />

								<li>
									<a href='${breadcrumbJSONObject.getString("url")}'>${breadcrumbJSONObject.getString("title")}</a>
								</li>
							</#list>
						</#if>

						<li>
							${navigationJSONObject.getJSONObject("self").getString("title")}
						</li>
					</ul>

					<div class="submit-feedback">
						<a
							class="text-decoration-none"
							href="http://discuss.liferay.com"
						>
							${languageUtil.get(locale, "submit-feedback", "Submit Feedback")}
							<@clay["icon"] symbol="message-boards" />
						</a>
					</div>
				</div>
			</div>
		</div>

		<div class="learn-article-wrapper">
			<div class="language-log learn-article-content">
				<#if (content.getData())??>
					${content.getData()}
				</#if>

				<#if showChildrenCards && childrenJSONArray.length() gt 0>
					<div class="learn-card-container">
						<#list 0..childrenJSONArray.length()-1 as i>
							<#assign childJSONObject = childrenJSONArray.getJSONObject(i) />

							<div class="learn-card">
								<a href="${childJSONObject.getString("url")}">
									<h4>${childJSONObject.getString("title")}</h4>
								</a>

								<#if childJSONObject.getJSONArray("children")?has_content>
									<#assign grandchildrenJSONArray = childJSONObject.getJSONArray("children") />

									<div class="mt-2 subsection">
										<#list 0..grandchildrenJSONArray.length()-1 as j>
											<#assign grandchildJSONObject = grandchildrenJSONArray.getJSONObject(j)! />

											<#if grandchildJSONObject?? && grandchildJSONObject["title"]?has_content && grandchildJSONObject["url"]?has_content>
												<a href="${grandchildJSONObject["url"]!}">
													${grandchildJSONObject["title"]!}
												</a>
											</#if>
										</#list>
									</div>
								</#if>
							</div>
						</#list>
					</div>
				</#if>

				<div class="learn-article-categories-tags">
					<#list taxonomyVocabularies as vocabulary>
						<div class="align-items-baseline d-flex mt-2">
							<div class="learn-article-category-title mr-2">
								${vocabulary}:
							</div>
							<#list taxonomyCategoriesMap[vocabulary]?sort_by("categoryName") as taxonomyCategory>
								<div class="learn-article-category-tag mr-2">
									<a
										class="label tag-container"
										href="/search?${vocabulary?lower_case?replace(" ", "-", "r")}=${taxonomyCategory.categoryId}"
									>
										<span>${taxonomyCategory.categoryName}</span>
									</a>
								</div>
							</#list>
						</div>
					</#list>
				</div>

				<div class="article-related-how-to">
					<#setting url_escaping_charset='UTF-8' />

					<#if (structuredContent.keywords?has_content && structuredContent.keywords?size > 0)>
						<#assign
							queryParams = {
								"fields": "dateModified,id,title",
								"filter": "(knowledgeArticleType eq 'howTo') and (status eq 0) and (sourceTeam eq 'Enablement')",
								"pageSize": "3",
								"search": structuredContent.keywords[0],
								"sort": "dateModified:desc"
							}
							queryParts = []
						/>

						<#list queryParams?keys as key>
							<#assign
								value = queryParams[key]

								queryParts = queryParts + ["${key?url}=${value?url}"]
							/>
						</#list>

						<#assign knowledgeArticles = restClient.get("/c/p2s3knowledgearticles/?" + queryParts?join('&')) />

						<#if (knowledgeArticles.totalCount)?has_content && (knowledgeArticles.totalCount > 0)>
							<div class="how-to-container">
								<div class="how-to-container-header">
									${languageUtil.get(locale, 'how-to-related-to-this-article')}
								</div>

								<div class="how-to-cards-container" id="how-to-cards-container">
									<#list knowledgeArticles.items as knowledgeArticle>
											<a class="how-to-card" href="${themeDisplay.getCanonicalURL()}/l/${knowledgeArticle.id}/">
											<div class="how-to-card-header">
												${knowledgeArticle.title!}
											</div>

											<div class="how-to-card-date-published">
												<#assign date = knowledgeArticle.dateModified?datetime("yyyy-MM-dd'T'HH:mm:ss'Z'") />

												${languageUtil.get(locale, 'published-date')}: ${date?string["MMM dd, yy hh:mm a"]}
											</div>
										</a>
									</#list>
								</div>
							</div>
						</#if>
					</#if>
				</div>
			</div>

			<div class="learn-article-page-nav">
				<ul class="nav nav-stacked toc" id="articleTOC"></ul>
			</div>
		</div>
	</div>
</article>

<style>
	.how-to-card {
		align-items: flex-start;
		background: var(--color-brand-primary-lighten-6, #FBFCFE);
		border: 1px solid var(--color-brand-primary-lighten-5, #E7EFFF);
		border-radius: 0.75rem;
		box-sizing: border-box;
		cursor: pointer;
		display: flex;
		flex-direction: column;
		gap: 0.5rem;
		isolation: isolate;
		justify-content: space-between;
		order: 0;
		padding: 1rem;
		width: 100%;
	}

	.how-to-card:hover {
		background: var(--color-action-primary-hover-10, #EDF3FE);
		border: 1px solid var(--color-action-primary-hover, #0053F0);
		border-radius: 0.625rem;
	}

	.how-to-card-date-published {
		align-items: center;
		align-self: stretch;
		color: var(--color-neutral-8, #54555F);
		display: flex;
		flex: none;
		flex-grow: 0;
		font-family: 'Source Sans 3';
		font-size: 0.75rem;
		font-style: normal;
		font-weight: 400;
		line-height: 1rem;
	}

	.how-to-card-header {
		align-items: center;
		align-self: stretch;
		color: var(--color-brand-primary, #0B5FFF);
		display: flex;
		flex: none;
		flex-grow: 0;
		font-family: 'Source Sans 3';
		font-size: 1rem;
		font-style: normal;
		font-weight: 600;
		line-height: 1.5rem;
	}

	.how-to-cards-container {
		display: flex;
		flex-direction: row;
		gap: 1rem;
		width: 100%;
	}

	.how-to-container {
		align-items: flex-start;
		align-self: stretch;
		background: var(--color-neutral-1, #F7F7F8);
		border-radius: 0.75rem;
		display: flex;
		flex: none;
		flex-direction: column;
		flex-grow: 0;
		gap: 1.5rem;
		isolation: isolate;
		margin: 1.5rem 0px;
		order: 0;
		padding: 1.5rem;
		width: 100%;
	}

	.how-to-container-header {
		align-items: center;
		color: var(--color-neutral-10, #282934);
		display: flex;
		flex: none;
		flex-grow: 0;
		font-family: 'Source Sans 3';
		font-size: 1.5rem;
		font-style: normal;
		font-weight: 700;
		line-height: 1.75rem;
	}

	@media only screen and (max-width: 1200px) {
		.how-to-cards-container {
			flex-direction: column;
		}
	}
</style>