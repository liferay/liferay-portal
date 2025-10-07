<#if entries?has_content>
	<ol class="breadcrumb">
		<#list entries as entry>
			<li class="breadcrumb-item">
				<#if entry?has_next>
					<a
						class="breadcrumb-link"

							<#if entry.isBrowsable()>
								href="${htmlUtil.escapeAttribute(entry.getURL()!"")}"
							</#if>
					>
						<span class="breadcrumb-text-truncate">${htmlUtil.escape(entry.getTitle())}</span>
					</a>
				<#else>
					<span aria-current="page" class="active breadcrumb-text-truncate">${htmlUtil.escape(entry.getTitle())}</span>
				</#if>
			</li>
		</#list>
	</ol>
</#if>