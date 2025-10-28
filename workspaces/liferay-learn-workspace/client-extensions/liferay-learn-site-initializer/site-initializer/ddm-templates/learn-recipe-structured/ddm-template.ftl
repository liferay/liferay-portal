<script>
	function checkScreenSize() {
		var collapsibleContent = document.getElementById('collapsibleContent');

		if (collapsibleContent) {
			if (window.innerWidth >= 768) {
				collapsibleContent.style.display = "block";
				return;
			}

			collapsibleContent.style.display = "none";
		}
	}

	document.addEventListener('DOMContentLoaded', function() {
		checkScreenSize();
	});

	window.addEventListener('resize', function() {
		checkScreenSize();
	});

	function copyToClipboard(button) {
		let codeToolbar = button.closest('.code-toolbar');

		let codeText = codeToolbar.querySelector('code.language-bash').innerText;

		if (codeText) {
			navigator.clipboard.writeText(
				codeText
			).then(
				function() {
					button.setAttribute('data-copy-state', 'copy-success');

					setTimeout(
						function() {
							button.setAttribute('data-copy-state', 'copy');
						},
						3000
					);
				}
			)
		}
	}

	window.addEventListener("load", function () {
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
</script>

<div class="learn-recipe-container">
	<div class="d-flex header-navigation justify-content-between mb-3 px-3">
		<div class="learn-recipe-breadcrumbs">
			<div>
				<div class="align-items-baseline d-flex justify-content-between">
					<ul aria-label="breadcrumb navigation" class="learn-recipe-breadcrumb" role="navigation">
						<li>
							<a href="/"><@clay["icon"] symbol="home-full" /></a>
						</li>
						<li>
							<a href='/search'><@liferay_ui["message"] key="recipes"/></a>
						</li>
						<li>
							${.vars["reserved-article-title"].data}
						</li>
					</ul>
				</div>
			</div>
		</div>

		<div class="component-button recipe-feedback text-break">
			<a class="btn btn-nm btn-link page-editor__editable" data-lfr-editable-id="link" data-lfr-editable-type="link" data-tooltip-floating="true" href="http://discuss.liferay.com" id="fragment-txnc-link">
				<@liferay_ui["message"] key="submit-feedback" />
			</a>
		</div>
	</div>

	<div class="container recipe-main">
		<div class="row">
			<div class="col-md-9 recipe-main" id="left-panel">
				<div class="disclaimer">
					<div class="container-fluid">
						<div class="row">
							<div class="col recipe-dialect">
								<@liferay_ui["message"] key="recipe" />
							</div>

							<div class="col text-right">
								<@liferay_ui["message"] key="published" />
								<#assign
									displayDate = .vars["reserved-article-display-date"].getData()?date("EEE, dd MMM yyyy HH:mm:ss Z")

									formattedDate = displayDate?string["LLLL dd, YYYY"]
								/>
								${formattedDate}
							</div>
						</div>
					</div>

					<div class="article-title">
						<h1>${.vars["reserved-article-title"].data}</h1>
					</div>

					<div>
						<p class="component-text mb-0 text-break text-paragraph">
							<@liferay_ui["message"] key="learn-recipe-header-text" />
						</p>
					</div>
				</div>

				<article>
					<a id="introduction" />

					<h3><@liferay_ui["message"] key="introduction"/></h3>

					<div>
						<#if (introduction.getData())??>
							${introduction.getData()}
						</#if>
					</div>

					<a id="prerequisites" />

					<h3><@liferay_ui["message"] key="prerequisites"/></h3>

					<#if Prerequisite.getSiblings()?? && Prerequisite.getSiblings()?has_content>
						<ul>
							<#list Prerequisite.getSiblings() as currentPrerequisite>
								<#if (currentPrerequisite.getData())??>
									<div>
										<li>${currentPrerequisite.getData()}</li>
									</div>
								</#if>
							</#list>
						</ul>
					<#else>
						<div>
							<@liferay_ui["message"] key="none" />
						</div>
					</#if>

					<a id="steps" />

					<h3><@liferay_ui["message"] key="steps"/></h3>

					<#if Steps.getSiblings()?has_content>
						<ol>
							<#list Steps.getSiblings() as currentStep>
								<li>
									${currentStep.Step.StepInstruction.getData()}

									<#if currentStep.Step.AdditionalNotes.getSiblings()?has_content>
										<#list currentStep.Step.AdditionalNotes.getSiblings() as currentNote>
											<#if currentNote?? && currentNote.NoteText.getData()?has_content>
												<div class="adm-block adm-${currentNote.NoteType.getData()}">
													<div class="adm-heading">
														<svg class="adm-icon">
															<use xlink:href="#adm-note"></use>
														</svg>

														<span>
															<@liferay_ui["message"] key="${currentNote.NoteType.getData()}" />
														</span>
													</div>

													<div class="adm-body">
														${currentNote.NoteText.getData()}
													</div>
												</div>
											</#if>
										</#list>
									</#if>

									<#if currentStep.Step.Resources.Image.getData()?has_content>
										<div class="mb-3">
										<img class="img-fluid rounded" height="75%" src="${currentStep.Step.Resources.Image.getData()}" width="75%" />
										</div>
									</#if>

									<#if currentStep.Step.Resources.code.getData()?has_content>
										<div class="code-toolbar">
											<pre class="language-bash" tabindex="0">
												<code class="language-bash">${currentStep.Step.Resources.code.getData()}</code>
											</pre>

											<div class="toolbar">
												<div class="toolbar-item">
													<button class="copy-to-clipboard-button" data-copy-state="copy" onclick="copyToClipboard(this)" type="button">
														<span>Copy</span>
													</button>
												</div>
											</div>
										</div>
									</#if>
								</li>
							</#list>
						</ol>
					</#if>

					<a id="conclusion" />

					<h3><@liferay_ui["message"] key="conclusion"/></h3>

					<#if (Conclusion.getData())??>
						${Conclusion.getData()}
					</#if>

					<a id="tips" />

					<h3><@liferay_ui["message"] key="tips"/></h3>

					<#if Tip.getSiblings()?has_content>
						<#list Tip.getSiblings() as currentTip>
							<#if (currentTip.getData())??>
								<div class="adm-block adm-tip">
									<div class="adm-heading">
										<svg class="adm-icon">
											<use xlink:href="#adm-tip"></use>
										</svg>

										<span><@liferay_ui["message"] key="tips"/></span>
									</div>

									<div class="adm-body">
										<p>${currentTip.getData()}</p>
									</div>
								</div>
							</#if>
						</#list>
					</#if>

					<#assign journalArticlePK = .vars["reserved-article-resource-prim-key"].getData()?number />

					<div class="page-nav-menu voting-box-bottom">
						<div class="d-flex flex-row align-items-center justify-content-evenly voting-box__content">
							<div>
								<@liferay_ui["message"] key="was-this-article-helpful" />
							</div>

							<@liferay_ratings["ratings"]
								className="com.liferay.journal.model.JournalArticle"
								classPK=journalArticlePK
								type="thumbs"
							/>
						</div>
					</div>
				</article>
			</div>

			<div class="col-md-3 recipe-sidemenu" id="right-panel">
				<div class="container-fluid">
					<div class="row sidemenu-header">
						<div class="col recipe-dialect">
							<@liferay_ui["message"] key="recipe" />
						</div>

						<div class="col reading-time text-right">
							<#if (timeToFinish.getData())??>
								<span>
									${timeToFinish.getData()} <@liferay_ui["message"] key="minutes" />
								</span>
							</#if>
						</div>
					</div>
				</div>

				<div class="page-nav-menu">
					<div class="component-button text-break">
						<a class="btn btn-link btn-nm" href="#introduction">
							<@liferay_ui["message"] key="introduction" />
						</a>
					</div>

					<div class="component-button text-break">
						<a class="btn btn-link btn-nm" href="#prerequisites">
							<@liferay_ui["message"] key="prerequisites" />
						</a>
					</div>

					<div class="component-button text-break">
						<a class="btn btn-link btn-nm" href="#steps">
							<@liferay_ui["message"] key="steps" />
						</a>
					</div>

					<div class="component-button text-break">
						<a class="btn btn-link btn-nm" href="#conclusion">
							<@liferay_ui["message"] key="conclusion" />
						</a>
					</div>

					<div class="component-button text-break">
						<a class="btn btn-link btn-nm" href="#tips">
							<@liferay_ui["message"] key="tips" />
						</a>
					</div>
				</div>
			</div>

			<#assign journalArticlePK = .vars["reserved-article-resource-prim-key"].getData()?number />

			<div class="page-nav-menu">
				<div class="asset-categories mb-3">
					<@liferay_asset["asset-categories-summary"]
						className="com.liferay.journal.model.JournalArticle"
						classPK=journalArticlePK
						displayStyle="simple-category"
					/>
				</div>

				<div class="asset-tags mb-3">
					<@liferay_asset["asset-tags-summary"]
						className="com.liferay.journal.model.JournalArticle"
						classPK=journalArticlePK
					/>
				</div>

				<div class="align-items-center d-flex flex-row">
					<div>
						<@liferay_ui["message"] key="was-this-article-helpful" />
					</div>

					<@liferay_ratings["ratings"]
						className="com.liferay.journal.model.JournalArticle"
						classPK=journalArticlePK
						type="thumbs"
					/>
				</div>
			</div>
		</div>
	</div>
</div>