<nav class="breadcrumb-container">
	<ul aria-label="breadcrumb navigation" class="learn-breadcrumb" role="navigation">
		<li>
			<a href="/">
				<@clay["icon"] symbol="home-full" />
			</a>
		</li>
		<li>
			<a href="/announcements">
				<@liferay_ui["message"] key="announcement" />
			</a>
		</li>
		<li>
			${ObjectField_title.getData()}
		</li>
	</ul>
</nav>

<div class="d-flex page-container">
	<div class="main-container my-3">
		<div class="header">
			<div class="asset-info d-flex">
				<p class="title">
					<@liferay_ui["message"] key="announcement" />
				</p>

				<p class="date">
					<#if (ObjectEntry_createDate.getData())??>
						<#assign rawDate = ObjectEntry_createDate.getData()?datetime("M/d/yy h:mm a") />
							<@liferay_ui["message"] key="published" /> ${rawDate?string("MMM. d, yyyy")}
					</#if>
				</p>
			</div>

			<div class="content-info mt-2">
				<h1>
					<#if (ObjectField_title.getData())??>
						${ObjectField_title.getData()}
					</#if>
				</h1>

				<div>
					<#if (ObjectField_description.getData())??>
						<p class="description">
							${ObjectField_description.getData()}
						</p>
					</#if>
				</div>
			</div>
		</div>

		<div class="content mt-3" id="content">
			<#if (ObjectField_content.getData())??>
				${ObjectField_content.getData()}
			</#if>

			<#if (.data_model["ObjectField_35642960#previewURL"].getData())?? && .data_model["ObjectField_35642960#previewURL"].getData() !="">
				<img alt="Image Preview" src="${.data_model["ObjectField_35642960#previewURL"].getData()}" />
			</#if>
		</div>
	</div>

	<div class="side-container">
		<div class="table-of-contents"></div>
	</div>
</div>

<script>
	document.addEventListener("DOMContentLoaded", function() {
		const contentContainer = document.getElementById("content");
		const tocContainer = document.querySelector(".table-of-contents");

		if (!contentContainer || !tocContainer) {
			return;
		}

		const headings = contentContainer.querySelectorAll("h3");

		if (headings.length === 0) {
			return;
		}

		const tocList = document.createElement("ul");

		headings.forEach((heading, index) => {
			const headingId = "heading-" + index;
			const link = document.createElement("a");
			const listItem = document.createElement("li");

			heading.id = headingId;

			link.href = "#" + headingId;
			link.textContent = heading.textContent;

			link.addEventListener("click", function() {
				tocList.querySelectorAll("li").forEach((li) => li.classList.remove("active"));
				listItem.classList.add("active");
			});

			listItem.appendChild(link);
			tocList.appendChild(listItem);
		});

		tocContainer.appendChild(tocList);
	});
</script>

<style>
	.admonion-container {
		border-radius: var(--border-radius-lg);
		margin-bottom: 1.5rem;
		padding: 1.5rem;

		p {
			margin-bottom: 0;
		}

		.admonion-title {
			display: flex;
			font-weight: 600;
			margin-bottom: 0;
			text-transform: uppercase;
		}
	}

	.admonion-type-danger {
		background-color: var(--color-state-error-lighten-2, #fbe3e3);

		.admonion-title {
			color: var(--color-state-error, #dA1414);
		}
	}

	.admonion-type-info {
	   background-color: #EFF2FA;

		admonion-title {
		  color: #4F6FB8;
		}
	}

	.admonion-type-note {
		background-color: var(--color-state-success-lighten-2, #e9f5e8);

		.admonion-title {
			color: var(--color-state-success-darken-1, #3b892f);
		}
	}

	.admonion-type-warning {
		background-color: var(--color-state-warning-lighten-2, #f7eae0);

		.admonion-title {
			color: var(--color-state-warning-darken-1, #944000);
		}
	}

	.asset-info {
		justify-content: space-between;

		p {
			margin-bottom: 0rem;
		}

		.date {
			color: var(--color-neutral-8, #54555f);
			font-size: 13px;
		}

		.title {
			color: var(--color-brand-primary, #0b5fff);
			font-size: 18px;
			font-weight: 600;

			&::before {
				content: url("data:image/svg+xml,%3Csvg%20width%3D%2216%22%20height%3D%2216%22%20viewBox%3D%220%200%2016%2016%22%20fill%3D%22none%22%20xmlns%3D%22http%3A//www.w3.org/2000/svg%22%3E%3Cmask%20id%3D%22mask0_2173_19132%22%20style%3D%22mask-type%3Aalpha%22%20maskUnits%3D%22userSpaceOnUse%22%20x%3D%220%22%20y%3D%220%22%20width%3D%2215%22%20height%3D%2215%22%3E%3Cmask%20id%3D%22path-1-inside-1_2173_19132%22%20fill%3D%22white%22%3E%3Crect%20x%3D%220.998535%22%20y%3D%223%22%20width%3D%224%22%20height%3D%226%22%20rx%3D%221%22/%3E%3C/mask%3E%3Crect%20x%3D%220.998535%22%20y%3D%223%22%20width%3D%224%22%20height%3D%226%22%20rx%3D%221%22%20stroke%3D%22%23D9D9D9%22%20stroke-width%3D%224%22%20mask%3D%22url(%23path-1-inside-1_2173_19132)%22/%3E%3Crect%20x%3D%223.99854%22%20y%3D%2210%22%20width%3D%223%22%20height%3D%225%22%20rx%3D%221%22%20fill%3D%22%23D9D9D9%22/%3E%3Cpath%20d%3D%22M13.9985%201.3877V10.6123L6.99854%208.2793V3.7207L13.9985%201.3877Z%22%20stroke%3D%22%23D9D9D9%22%20stroke-width%3D%222%22/%3E%3C/mask%3E%3Cg%20mask%3D%22url(%23mask0_2173_19132)%22%3E%3Crect%20y%3D%22-1%22%20width%3D%2216%22%20height%3D%2216%22%20fill%3D%22%230B5FFF%22/%3E%3C/g%3E%3C/svg%3E");
				display: inline-block;
				margin-right: 0.375rem;
				padding-top: 0.25rem;
				vertical-align: middle;
			}
		}
	}

	.component-html img {
		border-radius: 10px;
		width: 100%;
	}

	.content-info {
		.description {
			font-style: italic;
			margin-top: 1rem;
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
			padding: 0 0.25rem;
		}
	}

	.main-container {
		max-width: 1136px;
	}

	.side-container {
		align-self: flex-start;
		margin-left: 1rem;
		margin-top: 4rem;
		position: sticky;
		top: 12.5rem;
		min-width: 256px;
	}

	.table-of-contents {
		color: #333;
		font-size: 14px;
		max-width: 17.5rem;
		padding: 1rem;
	}

	.table-of-contents a {
		color: var(--color-neutral-6, #82828c);
		text-decoration: none;
		transition: color 0.3s ease;
	}

	.table-of-contents a:hover {
		color: var(--color-neutral-10, #282934);
	}

	.table-of-contents li {
		color: var(--color-neutral-10, #282934);
		height: 2rem;
		padding: 0.375rem 1rem;
		text-align: start;
	}

	.table-of-contents li.active {
		font-weight: bold;
		position: relative;
	}

	.table-of-contents li.active::before {
		background-color: var(--color-brand-primary, #0b5fff);
		border-radius: 0.125rem;
		content: '';
		height: 1.5rem;
		left: 0;
		position: absolute;
		top: 0.125rem;
		width: 0.25rem;
	}

	.table-of-contents ul {
		display: flex;
		flex-direction: column;
		gap: 0.5rem;
		list-style: none;
		margin: 0;
		padding-left: 0;
	}

	h1, h2, h3 {
	  color: var(--color-neutral-10, #282934);
	}

	html {
		scroll-behavior: smooth;
		scroll-padding-top: 11.25rem;
	}

	@media (max-width:1024px) {
		.asset-info {
			flex-direction: column;
			margin-bottom: 1rem;
		}

		.asset-info .title {
			margin-bottom: 0;
		}

		.side-container {
			display: none;
		}
	}
</style>