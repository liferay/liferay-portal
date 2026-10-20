import BasePage from 'shared/components/base-page';
import ClayIcon from 'shared/components/Icon';
import ClayLabel from '@clayui/label';
import ClayPanel from '@clayui/panel';
import ClaySticker from '@clayui/sticker';
import ClayTabs from '@clayui/tabs';
import getCN from 'classnames';
import React from 'react';
import {getMimeType} from '../components/mime-type';
import {Heading} from '@clayui/core';
import {sub} from 'shared/util/lang';
import {Text} from '@clayui/core';

interface IInfoPanelProps {
	data: {
		itemData: {
			assetCategories?: {
				id: string;
				name: string;
				vocabularyId: string;
			}[];
			assetTags?: {id: string; name: string}[];
			assetTitle: string;
			assetType: string;
			assetVocabularies?: {id: string; name: string}[];
			id: string;
			mimeType?: string;
		};
	} | null;
	onClose: () => void;
}

const InfoPanel: React.FC<IInfoPanelProps> = ({data, onClose}) => {
	const [activeTab, setActiveTab] = React.useState<number>(0);

	const mimeType = getMimeType({
		assetType: data?.itemData?.assetType,
		mimeType: data?.itemData?.mimeType,
	});

	return (
		<BasePage.Sidebar
			onOpenChange={(open) => !open && onClose()}
			open={!!data}
		>
			<BasePage.Sidebar.Header
				className="px-4"
				messages={{closeAriaLabel: Liferay.Language.get('close')}}
			>
				<div className="align-items-center d-flex">
					<ClaySticker
						className={getCN(
							'flex-shrink-0 mr-3',
							mimeType.className
						)}
						displayType="dark"
					>
						<ClayIcon symbol={mimeType.icon} />
					</ClaySticker>

					<BasePage.Sidebar.Title className="flex-grow-1 text-truncate">
						<Heading level={4} weight="semi-bold">
							{data?.itemData?.assetTitle ?? data?.itemData?.id}
						</Heading>
					</BasePage.Sidebar.Title>
				</div>
			</BasePage.Sidebar.Header>

			<BasePage.Sidebar.Body className="p-0">
				<ClayTabs active={activeTab} onActiveChange={setActiveTab}>
					<ClayTabs.Item
						innerProps={{
							'aria-controls': 'tabpanel-1',
						}}
					>
						{Liferay.Language.get('details')}
					</ClayTabs.Item>

					<ClayTabs.Item
						innerProps={{
							'aria-controls': 'tabpanel-2',
						}}
					>
						{Liferay.Language.get('categorization')}
					</ClayTabs.Item>
				</ClayTabs>

				<ClayTabs.Content activeIndex={activeTab} fade>
					<ClayTabs.TabPane aria-labelledby="tab-1">
						<ClayPanel
							collapsable={false}
							displayTitle={
								<ClayPanel.Header className="border-bottom">
									<ClayPanel.Title className="panel-title text-secondary">
										{Liferay.Language.get('metadata')}
									</ClayPanel.Title>
								</ClayPanel.Header>
							}
							displayType="unstyled"
						>
							<ClayPanel.Body>
								<div className="mb-2">
									<Text size={4} weight="semi-bold">
										{Liferay.Language.get('erc')}
									</Text>
								</div>

								<Text color="secondary" size={3}>
									{data?.itemData?.id}
								</Text>
							</ClayPanel.Body>
						</ClayPanel>
					</ClayTabs.TabPane>

					<ClayTabs.TabPane aria-labelledby="tab-2">
						<CategoriesInfoPanelContent
							items={data?.itemData?.assetCategories}
							vocabularies={data?.itemData?.assetVocabularies}
						/>

						<TagsInfoPanelContent
							items={data?.itemData?.assetTags}
						/>
					</ClayTabs.TabPane>
				</ClayTabs.Content>
			</BasePage.Sidebar.Body>
		</BasePage.Sidebar>
	);
};

interface CategoriesInfoPanelContentProps {
	items?: {id: string; name: string; vocabularyId: string}[];
	vocabularies?: {id: string; name: string}[];
}

const CategoriesInfoPanelContent: React.FC<CategoriesInfoPanelContentProps> = ({
	items,
	vocabularies,
}) => {
	const title = Liferay.Language.get('categories');
	const hasItems = !!items?.length;

	const grouped = hasItems
		? items!.reduce((map, category) => {
				const key = category.vocabularyId;

				return map.set(key, [...(map.get(key) ?? []), category]);
			}, new Map<string, typeof items>())
		: null;

	return (
		<ClayPanel
			collapsable={false}
			displayTitle={
				<ClayPanel.Header className="border-bottom">
					<ClayPanel.Title className="panel-title text-secondary">
						{title}
					</ClayPanel.Title>
				</ClayPanel.Header>
			}
			displayType="unstyled"
		>
			<ClayPanel.Body>
				{!hasItems && (
					<>
						<div className="mb-2">
							<Text size={4} weight="semi-bold">
								{sub(
									Liferay.Language.get(
										'no-x-were-found-for-this-asset'
									),
									[title]
								)}
							</Text>
						</div>

						<Text color="secondary" size={3}>
							{sub(
								Liferay.Language.get(
									'go-to-your-content-management-system-to-manage-x'
								),
								[title]
							)}
						</Text>
					</>
				)}

				{grouped &&
					vocabularies!.map(({id, name}) => {
						const categories = grouped.get(id);

						if (!categories?.length) {
							return null;
						}

						return (
							<div className="mb-3" key={id}>
								<div className="mb-2">
									<Text size={2} weight="semi-bold">
										{name}
									</Text>
								</div>

								{categories.map(
									({id: categoryId, name: categoryName}) => (
										<ClayLabel
											displayType="secondary"
											inverse
											key={categoryId}
											size="lg"
										>
											{categoryName}
										</ClayLabel>
									)
								)}
							</div>
						);
					})}
			</ClayPanel.Body>
		</ClayPanel>
	);
};

interface TagsInfoPanelContentProps {
	items?: {id: string; name: string}[];
}

const TagsInfoPanelContent: React.FC<TagsInfoPanelContentProps> = ({items}) => {
	const title = Liferay.Language.get('tags');

	return (
		<ClayPanel
			collapsable={false}
			displayTitle={
				<ClayPanel.Header className="border-bottom">
					<ClayPanel.Title className="panel-title text-secondary">
						{title}
					</ClayPanel.Title>
				</ClayPanel.Header>
			}
			displayType="unstyled"
		>
			<ClayPanel.Body>
				{!items?.length && (
					<>
						<div className="mb-2">
							<Text size={4} weight="semi-bold">
								{sub(
									Liferay.Language.get(
										'no-x-were-found-for-this-asset'
									),
									[title]
								)}
							</Text>
						</div>

						<Text color="secondary" size={3}>
							{sub(
								Liferay.Language.get(
									'go-to-your-content-management-system-to-manage-x'
								),
								[title]
							)}
						</Text>
					</>
				)}

				{!!items?.length &&
					items.map(({id, name}) => (
						<ClayLabel
							displayType="secondary"
							inverse
							key={id}
							size="lg"
						>
							{name}
						</ClayLabel>
					))}
			</ClayPanel.Body>
		</ClayPanel>
	);
};

export {InfoPanel};
