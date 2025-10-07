<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<h3>Image Cards</h3>

<%
ClaySampleImageCard claySampleImageCard = new ClaySampleImageCard();
%>

<clay:row>
	<clay:col
		md="4"
	>
		<clay:image-card
			actionDropdownItems="<%= claySampleImageCard.getActionDropdownItems() %>"
			href="<%= claySampleImageCard.getHref() %>"
			imageAlt="thumbnail"
			imageSrc="https://images.unsplash.com/photo-1506976773555-b3da30a63b57"
			subtitle="<%= claySampleImageCard.getSubtitle() %>"
			title="Madrid"
		/>
	</clay:col>

	<clay:col
		md="4"
	>
		<clay:image-card
			actionDropdownItems="<%= claySampleImageCard.getActionDropdownItems() %>"
			icon="<%= claySampleImageCard.getIcon() %>"
			subtitle="<%= claySampleImageCard.getSubtitle() %>"
			title="<%= _SVG_FILE_TITLE %>"
		/>
	</clay:col>

	<clay:col
		md="4"
	>
		<clay:image-card
			actionDropdownItems="<%= claySampleImageCard.getActionDropdownItems() %>"
			subtitle="Author Action"
			title="<%= _SVG_FILE_TITLE %>"
		/>
	</clay:col>
</clay:row>

<div class="h4">Image Card with Sticker</div>

<clay:row>
	<clay:col
		md="4"
	>
		<clay:image-card
			actionDropdownItems="<%= claySampleImageCard.getActionDropdownItems() %>"
			href="<%= claySampleImageCard.getHref() %>"
			imageAlt="thumbnail"
			imageSrc="https://images.unsplash.com/photo-1499310226026-b9d598980b90"
			stickerLabel="<%= claySampleImageCard.getStickerLabel() %>"
			stickerStyle="<%= claySampleImageCard.getStickerStyle() %>"
			subtitle="<%= claySampleImageCard.getSubtitle() %>"
			title="California"
		/>
	</clay:col>

	<clay:col
		md="4"
	>
		<clay:image-card
			actionDropdownItems="<%= claySampleImageCard.getActionDropdownItems() %>"
			icon="<%= claySampleImageCard.getIcon() %>"
			stickerLabel="SVG"
			stickerStyle="<%= claySampleImageCard.getStickerStyle() %>"
			subtitle="<%= claySampleImageCard.getSubtitle() %>"
			title="<%= _SVG_FILE_TITLE %>"
		/>
	</clay:col>

	<clay:col
		md="4"
	>
		<clay:image-card
			actionDropdownItems="<%= claySampleImageCard.getActionDropdownItems() %>"
			stickerLabel="<%= claySampleImageCard.getStickerLabel() %>"
			stickerStyle="<%= claySampleImageCard.getStickerStyle() %>"
			subtitle="<%= claySampleImageCard.getStickerStyle() %>"
			title="<%= _PNG_FILE_TITLE %>"
		/>
	</clay:col>
</clay:row>

<div class="h4">Image Card with Sticker Shape</div>

<clay:row>
	<clay:col
		md="4"
	>
		<clay:image-card
			actionDropdownItems="<%= claySampleImageCard.getActionDropdownItems() %>"
			href="<%= claySampleImageCard.getHref() %>"
			imageAlt="<%= claySampleImageCard.getImageAlt() %>"
			imageSrc="https://images.unsplash.com/photo-1490900245048-1bf948e866c2"
			stickerLabel="<%= claySampleImageCard.getStickerLabel() %>"
			stickerShape="circle"
			stickerStyle="<%= claySampleImageCard.getStickerStyle() %>"
			subtitle="<%= claySampleImageCard.getSubtitle() %>"
			title="California"
		/>
	</clay:col>

	<clay:col
		md="4"
	>
		<clay:image-card
			actionDropdownItems="<%= claySampleImageCard.getActionDropdownItems() %>"
			icon="<%= claySampleImageCard.getIcon() %>"
			stickerLabel="SVG"
			stickerShape="circle"
			stickerStyle="warning"
			subtitle="<%= claySampleImageCard.getSubtitle() %>"
			title="<%= _SVG_FILE_TITLE %>"
		/>
	</clay:col>

	<clay:col
		md="4"
	>
		<clay:image-card
			actionDropdownItems="<%= claySampleImageCard.getActionDropdownItems() %>"
			stickerImageAlt="Alt Text"
			stickerImageSrc="https://images.unsplash.com/photo-1502290822284-9538ef1f1291"
			stickerLabel="PNG"
			stickerShape="<%= claySampleImageCard.getStickerShape() %>"
			stickerStyle="info"
			subtitle="<%= claySampleImageCard.getSubtitle() %>"
			title="<%= _PNG_FILE_TITLE %>"
		/>
	</clay:col>
</clay:row>

<div class="h4">Image Card with Labels</div>

<clay:row>
	<clay:col
		md="4"
	>
		<clay:image-card
			actionDropdownItems="<%= claySampleImageCard.getActionDropdownItems() %>"
			href="<%= claySampleImageCard.getHref() %>"
			imageAlt="thumbnail"
			imageSrc="https://images.unsplash.com/photo-1503703294279-c83bdf7b4bf4"
			labels="<%= claySampleImageCard.getLabels() %>"
			stickerLabel="<% claySampleImageCard.getStickerLabel() %>"
			stickerStyle="danger"
			subtitle="<%= claySampleImageCard.getSubtitle() %>"
			title="Beetle"
		/>
	</clay:col>

	<clay:col
		md="4"
	>
		<clay:image-card
			actionDropdownItems="<%= claySampleImageCard.getActionDropdownItems() %>"
			icon="camera"
			labels="<%= claySampleImageCard.getLabels() %>"
			stickerLabel="SVG"
			stickerStyle="warning"
			subtitle="<%= claySampleImageCard.getSubtitle() %>"
			title="<%= _SVG_FILE_TITLE %>"
		/>
	</clay:col>

	<clay:col
		md="4"
	>
		<clay:image-card
			actionDropdownItems="<%= claySampleImageCard.getActionDropdownItems() %>"
			labels="<%= claySampleImageCard.getLabels() %>"
			stickerLabel="PNG"
			stickerStyle="<%= claySampleImageCard.getStickerStyle() %>"
			subtitle="<%= claySampleImageCard.getSubtitle() %>"
			title="<%= _SVG_FILE_TITLE %>"
		/>
	</clay:col>
</clay:row>

<div class="h4">Selectable Image Card</div>

<clay:row>
	<clay:col
		md="4"
	>
		<clay:image-card
			actionDropdownItems="<%= claySampleImageCard.getActionDropdownItems() %>"
			href="<%= claySampleImageCard.getHref() %>"
			imageAlt="thumbnail"
			imageSrc="https://images.unsplash.com/photo-1510360638044-c6f328b5d283"
			labels="<%= claySampleImageCard.getLabels() %>"
			selectable="<%= true %>"
			selected="<%= true %>"
			stickerLabel="<%= claySampleImageCard.getStickerLabel() %>"
			stickerStyle="<%= claySampleImageCard.getStickerStyle() %>"
			subtitle="<%= claySampleImageCard.getSubtitle() %>"
			title="Beetle"
		/>
	</clay:col>

	<clay:col
		data-qa-id="image-card-icon-block"
		md="4"
	>
		<clay:image-card
			actionDropdownItems="<%= claySampleImageCard.getActionDropdownItems() %>"
			icon="camera"
			labels="<%= claySampleImageCard.getLabels() %>"
			selectable="<%= true %>"
			selected="<%= false %>"
			stickerLabel="SVG"
			stickerStyle="warning"
			subtitle="Author Action"
			title="<%= _SVG_FILE_TITLE %>"
		/>
	</clay:col>

	<clay:col
		md="4"
	>
		<clay:image-card
			actionDropdownItems="<%= claySampleImageCard.getActionDropdownItems() %>"
			imageAlt="<%= claySampleImageCard.getTitle() %>"
			imageSrc="https://images.unsplash.com/photo-1525151212033-377d12acc381"
			labels="<%= claySampleImageCard.getLabels() %>"
			selectable="<%= true %>"
			selected="<%= true %>"
			stickerLabel="PNG"
			stickerStyle="<%= claySampleImageCard.getStickerStyle() %>"
			subtitle="<%= claySampleImageCard.getSubtitle() %>"
			title="<%= _SVG_FILE_TITLE %>"
		/>
	</clay:col>
</clay:row>

<div class="h4">Image Card Using Model</div>

<clay:row>
	<clay:col
		md="6"
	>

		<%
		claySampleImageCard.setHref("#1");
		claySampleImageCard.setImageSrc("https://images.unsplash.com/photo-1503891450247-ee5f8ec46dc3");
		claySampleImageCard.setSelectable(false);
		claySampleImageCard.setTitle("California");
		%>

		<clay:image-card
			imageCard="<%= claySampleImageCard %>"
		/>
	</clay:col>

	<clay:col
		md="6"
	>

		<%
		claySampleImageCard.setHref("#2");
		claySampleImageCard.setImageSrc("https://images.unsplash.com/photo-1485970671356-ff9156bd4a98");
		claySampleImageCard.setSelected(true);
		claySampleImageCard.setTitle("Mountains");
		%>

		<clay:image-card
			imageCard="<%= claySampleImageCard %>"
		/>
	</clay:col>
</clay:row>

<div class="h4">File Cards</div>

<%
ClaySampleFileCard claySampleFileCard = new ClaySampleFileCard();
%>

<clay:row>
	<clay:col
		md="4"
	>
		<clay:file-card
			actionDropdownItems="<%= claySampleFileCard.getActionDropdownItems() %>"
			disabled="<%= true %>"
			icon="user"
			labels="<%= claySampleFileCard.getLabels() %>"
			stickerLabel="PDF"
			stickerStyle="<%= claySampleFileCard.getStickerStyle() %>"
			subtitle="<%= claySampleFileCard.getSubtitle() %>"
			title="<%= _PDF_FILE_TITLE %>"
		/>
	</clay:col>

	<clay:col
		md="4"
	>
		<clay:file-card
			actionDropdownItems="<%= claySampleFileCard.getActionDropdownItems() %>"
			labels="<%= claySampleFileCard.getLabels() %>"
			selectable="<%= false %>"
			stickerLabel="MP3"
			stickerStyle="warning"
			subtitle="Jimi Hendrix"
			title="<%= _MP3_FILE_TITLE %>"
		/>
	</clay:col>

	<clay:col
		md="4"
	>
		<clay:file-card
			actionDropdownItems="<%= claySampleFileCard.getActionDropdownItems() %>"
			icon="list"
			labels="<%= claySampleFileCard.getLabels() %>"
			selectable="<%= true %>"
			selected="<%= true %>"
			stickerLabel="<%= claySampleFileCard.getStickerLabel() %>"
			stickerStyle="<%= claySampleFileCard.getStickerStyle() %>"
			subtitle="<%= claySampleFileCard.getSubtitle() %>"
			title="<%= _DOC_FILE_TITLE %>"
		/>
	</clay:col>
</clay:row>

<div class="h4">File Cards Using Model</div>

<clay:row>
	<clay:col
		md="6"
	>

		<%
		claySampleFileCard.setDisabled(true);
		claySampleFileCard.setStickerLabel("PDF");
		claySampleFileCard.setStickerStyle("info");
		claySampleFileCard.setSubtitle("Another PDF document");
		claySampleFileCard.setTitle(_PDF_FILE_TITLE);
		%>

		<clay:file-card
			fileCard="<%= claySampleFileCard %>"
		/>
	</clay:col>

	<clay:col
		md="6"
	>

		<%
		claySampleFileCard.setDisabled(false);
		claySampleFileCard.setIcon("list");
		claySampleFileCard.setSelectable(true);
		claySampleFileCard.setSelected(true);
		claySampleFileCard.setStickerLabel("MP3");
		claySampleFileCard.setStickerStyle("warning");
		claySampleFileCard.setSubtitle("More music");
		claySampleFileCard.setTitle(_MP3_FILE_TITLE);
		%>

		<clay:file-card
			fileCard="<%= claySampleFileCard %>"
		/>
	</clay:col>
</clay:row>

<div class="h4">User Cards</div>

<%
ClaySampleUserCard claySampleUserCard = new ClaySampleUserCard();
%>

<clay:row>
	<clay:col
		data-qa-id="image-card-block"
		md="4"
	>
		<clay:user-card
			name="<%= claySampleUserCard.getName() %>"
			selectable="<%= false %>"
			subtitle="<%= claySampleUserCard.getSubtitle() %>"
			userColorClass="<%= claySampleUserCard.getUserColorClass() %>"
		/>
	</clay:col>

	<clay:col
		data-qa-id="image-card-block"
		md="4"
	>
		<clay:user-card
			actionDropdownItems="<%= claySampleUserCard.getActionDropdownItems() %>"
			icon="picture"
			name="<%= claySampleUserCard.getName() %>"
			selectable="<%= false %>"
			subtitle="<%= claySampleUserCard.getSubtitle() %>"
			userColorClass="danger"
		/>
	</clay:col>

	<clay:col
		data-qa-id="image-card-icon-block"
		md="4"
	>
		<clay:user-card
			actionDropdownItems="<%= claySampleUserCard.getActionDropdownItems() %>"
			disabled="<%= true %>"
			imageAlt="<%= claySampleUserCard.getImageAlt() %>"
			imageSrc="https://images.unsplash.com/photo-1502290822284-9538ef1f1291"
			name="<%= claySampleUserCard.getName() %>"
			selectable="<%= false %>"
			subtitle="<%= claySampleUserCard.getSubtitle() %>"
		/>
	</clay:col>
</clay:row>

<div class="h4">Selectable User Cards</div>

<clay:row>
	<clay:col
		data-qa-id="image-card-block"
		md="4"
	>
		<clay:user-card
			disabled="<%= true %>"
			name="<%= claySampleUserCard.getName() %>"
			subtitle="<%= claySampleUserCard.getSubtitle() %>"
			userColorClass="<%= claySampleUserCard.getUserColorClass() %>"
		/>
	</clay:col>

	<clay:col
		data-qa-id="image-card-block"
		md="4"
	>
		<clay:user-card
			actionDropdownItems="<%= claySampleUserCard.getActionDropdownItems() %>"
			icon="picture"
			name="<%= claySampleUserCard.getName() %>"
			selected="<%= true %>"
			subtitle="<%= claySampleUserCard.getSubtitle() %>"
			userColorClass="danger"
		/>
	</clay:col>

	<clay:col
		data-qa-id="image-card-icon-block"
		md="4"
	>
		<clay:user-card
			actionDropdownItems="<%= claySampleUserCard.getActionDropdownItems() %>"
			imageAlt="<%= claySampleUserCard.getImageAlt() %>"
			imageSrc="https://images.unsplash.com/photo-1502290822284-9538ef1f1291"
			name="<%= claySampleUserCard.getName() %>"
			subtitle="<%= claySampleUserCard.getSubtitle() %>"
		/>
	</clay:col>
</clay:row>

<div class="h4">User Cards Using Model</div>

<clay:row>
	<clay:col
		data-qa-id="image-card-block"
		md="4"
	>

		<%
		claySampleUserCard.setDisabled(true);
		claySampleUserCard.setSelectable(false);
		%>

		<clay:user-card
			userCard="<%= claySampleUserCard %>"
		/>
	</clay:col>

	<clay:col
		data-qa-id="image-card-block"
		md="4"
	>

		<%
		claySampleUserCard.setDisabled(false);
		claySampleUserCard.setIcon("picture");
		claySampleUserCard.setUserColorClass("danger");
		%>

		<clay:user-card
			userCard="<%= claySampleUserCard %>"
		/>
	</clay:col>

	<clay:col
		data-qa-id="image-card-icon-block"
		md="4"
	>

		<%
		claySampleUserCard.setImageSrc("https://images.unsplash.com/photo-1502290822284-9538ef1f1291");
		%>

		<clay:user-card
			userCard="<%= claySampleUserCard %>"
		/>
	</clay:col>
</clay:row>

<div class="h4">Selectable User Cards Using Display Context</div>

<clay:row>
	<clay:col
		data-qa-id="image-card-block"
		md="4"
	>

		<%
		claySampleUserCard.setDisabled(true);
		claySampleUserCard.setIcon("user");
		claySampleUserCard.setImageSrc(null);
		claySampleUserCard.setSelectable(true);
		claySampleUserCard.setUserColorClass("info");
		%>

		<clay:user-card
			userCard="<%= claySampleUserCard %>"
		/>
	</clay:col>

	<clay:col
		data-qa-id="image-card-block"
		md="4"
	>

		<%
		claySampleUserCard.setDisabled(false);
		claySampleUserCard.setIcon("picture");
		claySampleUserCard.setUserColorClass("danger");
		%>

		<clay:user-card
			userCard="<%= claySampleUserCard %>"
		/>
	</clay:col>

	<clay:col
		data-qa-id="image-card-icon-block"
		md="4"
	>

		<%
		claySampleUserCard.setImageSrc("https://images.unsplash.com/photo-1502290822284-9538ef1f1291");
		%>

		<clay:user-card
			userCard="<%= claySampleUserCard %>"
		/>
	</clay:col>
</clay:row>

<div class="h4">Horizontal Cards</div>

<clay:row>
	<clay:col
		data-qa-id="simpleHorizontalCard"
		md="3"
	>
		<clay:horizontal-card
			title="ReallySuperInsanelyJustIncrediblyLongAndTotallyNotPossibleWordButWeAreReallyTryingToCoverAllOurBasesHereJustInCaseSomeoneIsNutsAsPerUsual"
		/>
	</clay:col>

	<clay:col
		data-qa-id="selectableHorizontalCard"
		md="3"
	>
		<clay:horizontal-card
			actionDropdownItems="<%= cardsDisplayContext.getActionDropdownItems() %>"
			disabled="<%= true %>"
			selectable="<%= true %>"
			title="ReallySuperInsanelyJustIncrediblyLongAndTotallyNotPossibleWordButWeAreReallyTryingToCoverAllOurBasesHereJustInCaseSomeoneIsNutsAsPerUsual"
		/>
	</clay:col>

	<clay:col
		data-qa-id="modelHorizontalCard"
		md="2"
	>
		<clay:horizontal-card
			horizontalCard="<%= new ClaySampleHorizontalCard() %>"
		/>
	</clay:col>

	<clay:col
		data-qa-id="modelHorizontalCard"
		md="2"
	>
		<clay:horizontal-card
			title="a"
			translated="<%= true %>"
		/>
	</clay:col>

	<clay:col
		data-qa-id="modelHorizontalCard"
		md="2"
	>
		<clay:horizontal-card
			title="a"
			translated="<%= false %>"
		/>
	</clay:col>
</clay:row>

<div class="h4">Vertical Cards</div>

<%
ClaySampleVerticalCard claySampleVerticalCard = new ClaySampleVerticalCard();
%>

<clay:row>
	<clay:col
		md="6"
	>
		<clay:vertical-card
			actionDropdownItems="<%= claySampleVerticalCard.getActionDropdownItems() %>"
			imageAlt="Elephant"
			imageSrc="https://images.unsplash.com/photo-1502290822284-9538ef1f1291"
			labels="<%= claySampleVerticalCard.getLabels() %>"
			selectable="<%= false %>"
			stickerLabel="DXP"
			stickerStyle="info"
			subtitle="<%= claySampleVerticalCard.getSubtitle() %>"
			title="ReallySuperInsanelyJustIncrediblyLongAndTotallyNotPossibleWordButWeAreReallyTryingToCoverAllOurBasesHereJustInCaseSomeoneIsNutsAsPerUsual"
		/>
	</clay:col>

	<clay:col
		md="6"
	>
		<clay:vertical-card
			actionDropdownItems="<%= claySampleVerticalCard.getActionDropdownItems() %>"
			icon="camera"
			labels="<%= claySampleVerticalCard.getLabels() %>"
			selectable="<%= true %>"
			selected="<%= true %>"
			stickerLabel="JPG"
			stickerStyle="<%= claySampleVerticalCard.getStickerStyle() %>"
			subtitle="<%= claySampleVerticalCard.getSubtitle() %>"
			title="ReallySuperInsanelyJustIncrediblyLongAndTotallyNotPossibleWordButWeAreReallyTryingToCoverAllOurBasesHereJustInCaseSomeoneIsNutsAsPerUsual"
		/>
	</clay:col>
</clay:row>

<div class="h4">Vertical Cards Using Model</div>

<clay:row>
	<clay:col
		md="6"
	>

		<%
		claySampleVerticalCard.setImageAlt(claySampleVerticalCard.getTitle());
		claySampleVerticalCard.setImageSrc("https://images.unsplash.com/photo-1554939437-ecc492c67b78");
		claySampleVerticalCard.setSelectable(true);
		claySampleVerticalCard.setStickerLabel("MAD");
		claySampleVerticalCard.setStickerStyle("success");
		claySampleVerticalCard.setSubtitle("A beautiful city");
		claySampleVerticalCard.setTitle("Madrid");
		%>

		<clay:vertical-card
			verticalCard="<%= claySampleVerticalCard %>"
		/>
	</clay:col>

	<clay:col
		md="6"
	>

		<%
		claySampleVerticalCard.setDisabled(true);
		claySampleVerticalCard.setImageSrc(null);
		claySampleVerticalCard.setSelectable(false);
		claySampleVerticalCard.setSelected(true);
		claySampleVerticalCard.setStickerLabel(null);
		claySampleVerticalCard.setStickerStyle("warning");
		claySampleVerticalCard.setSubtitle("This card is disabled");
		claySampleVerticalCard.setTitle(null);
		%>

		<clay:vertical-card
			verticalCard="<%= claySampleVerticalCard %>"
		/>
	</clay:col>
</clay:row>

<div class="h4">Navigation Cards</div>

<clay:row>
	<clay:col
		md="3"
	>
		<clay:navigation-card
			icon="page"
			propsTransformer="{ClaySampleNavigationCardPropsTransformer} from frontend-taglib-clay-sample-web"
			small="<%= true %>"
			title="add-page"
		/>
	</clay:col>

	<clay:col
		md="3"
	>
		<clay:navigation-card
			href="#"
			icon="page"
			small="<%= true %>"
			title="children-pages"
		/>
	</clay:col>

	<clay:col
		md="3"
	>
		<clay:navigation-card
			description="choose-a-display-page-template"
			icon="page-template"
			title="private-page"
		/>
	</clay:col>

	<clay:col
		md="3"
	>
		<clay:navigation-card
			navigationCard="<%= new ClaySampleNavigationCard() %>"
		/>
	</clay:col>
</clay:row>

<%!
private static final String _DOC_FILE_TITLE = "deliverable.doc";

private static final String _MP3_FILE_TITLE = "deliverable.mp3";

private static final String _PDF_FILE_TITLE = "deliverable.pdf";

private static final String _PNG_FILE_TITLE = "lexicon.icon.camera.png";

private static final String _SVG_FILE_TITLE = "lexicon.icon.camera.svg";
%>