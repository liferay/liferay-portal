import ClayIcon from 'shared/components/Icon';
import ClayLabel from '@clayui/label';
import ClayPanel from '@clayui/panel';
import ClaySticker from '@clayui/sticker';
import ClayTabs from '@clayui/tabs';
import getCN from 'classnames';
import React, {useRef} from 'react';
import {ClayButtonWithIcon} from '@clayui/button';
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
	const sidePanelRef = useRef(null);
	const [activeTab, setActiveTab] = React.useState<number>(0);

	const mimeType = getMimeType({
		assetType: data?.itemData?.assetType,
		mimeType: data?.itemData?.mimeType,
	});

	return (
		<div
			className={getCN(
				'info-panel-root  c-slideout c-slideout-absolute c-slideout-push c-slideout-end',
				{
					'c-slideout-shown': !!data,
				}
			)}
			id="infoPanel"
			ref={sidePanelRef}
		>
			<div
				className={getCN('sidebar sidebar-light', {
					'c-slideout-show': !!data,
				})}
				style={{width: 472}}
			>
				<div className="sidebar-header">
					<div className="autofit-row">
						<div className="autofit-col autofit-col-expand">
							<span className="component-title">
								<div className="align-items-center d-flex">
									<span className="mr-3">
										<ClaySticker
											className={mimeType.className}
											displayType="dark"
										>
											<ClayIcon symbol={mimeType.icon} />
										</ClaySticker>
									</span>

									<Heading level={4} weight="semi-bold">
										{data?.itemData?.assetTitle ??
											data?.itemData?.id}
									</Heading>
								</div>
							</span>
						</div>
						<div className="autofit-col">
							<ClayButtonWithIcon
								className="close"
								displayType="unstyled"
								onClick={() => onClose()}
								symbol="times"
							/>
						</div>
					</div>
				</div>

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
			</div>
		</div>
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
											className="label-lg"
											key={categoryId}
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
						<ClayLabel className="label-lg" key={id}>
							{name}
						</ClayLabel>
					))}
			</ClayPanel.Body>
		</ClayPanel>
	);
};

export {InfoPanel};
