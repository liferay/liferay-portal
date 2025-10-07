<div>
	<p class="m-0 text-paragraph">
		${CommerceDescription.getData()}
	</p>

	<#if (MoreDetails.getData())??>
		<p class="m-0 text-neutral-7 text-paragraph-sm">
			${MoreDetails.getData()}

			<#if (CommerceUrl.getData())??>
				&nbsp;

				<a href="${friendlyURLs[themeDisplay.getLanguageId()]!""}">
					<a href="${CommerceUrl.getData()}">
						<u>${CommerceTitleUrl.getData()}</u>
					</a>
				</a>
			</#if>
		</p>
	</#if>
</div>