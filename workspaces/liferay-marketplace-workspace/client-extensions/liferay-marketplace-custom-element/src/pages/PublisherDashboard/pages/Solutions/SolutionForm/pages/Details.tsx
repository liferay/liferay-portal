/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClaySelect} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import {useState} from 'react';

import Form from '../../../../../../components/MarketplaceForm';
import {
	BlockDirections,
	SolutionTypes,
	useSolutionContext,
} from '../../../../../../context/SolutionContext';
import i18n from '../../../../../../i18n';
import IconsBlock from '../../components/Blocks/IconBlock';
import ImagesGrid from '../../components/Blocks/ImagesGrid';
import SingleImage from '../../components/Blocks/SingleImage';
import TextAndImages from '../../components/Blocks/TextAndImages';
import TextAndVideos from '../../components/Blocks/TextAndVideo';
import TextBlock from '../../components/Blocks/TextBlock';

export const blockTypes = {
	'icons-block': IconsBlock,
	'images-grid-block': ImagesGrid,
	'single-image-block': SingleImage,
	'text-block': TextBlock,
	'text-images-block': TextAndImages,
	'text-video-block': TextAndVideos,
} as const;

const items = [
	{label: 'Choose an option', value: ''},
	{label: 'Text & Images Block', value: 'text-images-block'},
	{label: 'Text & Video Block', value: 'text-video-block'},
	{label: 'Text Block', value: 'text-block'},
];

const Details = () => {
	const [{details: blocks}, dispatch] = useSolutionContext();
	const {observer, onOpenChange, open} = useModal();
	const [selectedBlock, setSelectedBlock] = useState('');

	return (
		<div className="solutions-form-details">
			<Form.Label className="mt-3" htmlFor="minimum-blocks" required>
				Add a minimum of 2 blocks
			</Form.Label>

			{blocks.map((block, index) => {
				const Component = (blockTypes as any)[block.type];

				const handleMoveOrDeleteBlock = (
					direction: BlockDirections
				) => {
					dispatch({
						payload: {
							direction,
							index,
						},
						type: SolutionTypes.SET_BLOCK_MOVE,
					});
				};

				const dropdownItems = [
					{
						disabled: index === 0,
						name: i18n.translate('move-to-top'),
						onClick: () =>
							handleMoveOrDeleteBlock(
								BlockDirections.MOVE_TO_TOP
							),
					},
					{
						disabled: index === 0,
						name: i18n.translate('move-up'),
						onClick: () =>
							handleMoveOrDeleteBlock(BlockDirections.MOVE_UP),
					},
					{
						disabled: index === blocks.length - 1,
						name: i18n.translate('move-down'),
						onClick: () =>
							handleMoveOrDeleteBlock(BlockDirections.MOVE_DOWN),
					},
					{
						disabled: index === blocks.length - 1,
						name: i18n.translate('move-to-bottom'),
						onClick: () =>
							handleMoveOrDeleteBlock(
								BlockDirections.MOVE_TO_BOTTOM
							),
					},
					{
						name: i18n.translate('delete'),
						onClick: () =>
							handleMoveOrDeleteBlock(BlockDirections.DELETE),
					},
				];

				return (
					<Form.SectionWithControllers
						dropdownItems={dropdownItems}
						index={index}
						key={index}
						name={
							items.find(({value}) => value === block.type)
								?.label as string
						}
						onArrowClick={(direction) => {
							handleMoveOrDeleteBlock(direction);
						}}
						position={blocks.length}
					>
						<Component
							block={block}
							key={index}
							onChange={(content: any) => {
								dispatch({
									payload: {
										block: {
											...blocks[index],
											content: {
												...blocks[index].content,
												...content,
											},
										},
										index,
									},
									type: SolutionTypes.SET_UPDATE_BLOCK,
								});
							}}
							onDeleteImage={(payload: string) =>
								dispatch({
									payload,
									type: SolutionTypes.SET_DELETE_IMAGE,
								})
							}
						/>
					</Form.SectionWithControllers>
				);
			})}

			<ClayButton
				className="align-items-center content-block d-flex flex-row justify-content-center mt-4 w-100"
				displayType="secondary"
				onClick={() => onOpenChange(true)}
			>
				<span className="d-flex flex-row inline-item inline-item-before">
					<ClayIcon symbol="plus" />
				</span>
				Add Content Block
			</ClayButton>

			{open && (
				<ClayModal center observer={observer}>
					<ClayModal.Body className="mb-1">
						<h1 className="d-flex justify-content-between">
							Select Content Block
							<ClayButtonWithIcon
								aria-label="Close"
								className="inline-item"
								displayType="unstyled"
								onClick={() => onOpenChange(false)}
								size="sm"
								symbol="times"
								title="Close"
							/>
						</h1>

						<p className="text-black-50">
							Choose one of the following content blocks
						</p>
						<Form.Label
							className="mt-5"
							htmlFor="choose-block"
							required
						>
							Choose Block
						</Form.Label>

						<ClaySelect
							aria-label="Select Label"
							id="choose-block"
							onChange={({target}) => {
								setSelectedBlock(target.value);
							}}
							value={selectedBlock}
						>
							{items.map((item, index) => (
								<ClaySelect.Option
									key={index}
									label={item.label}
									value={item.value}
								/>
							))}
						</ClaySelect>

						<div className="align-items-end d-flex justify-content-end mt-8">
							<ClayButton
								className="mr-2"
								displayType="secondary"
								onClick={() => onOpenChange(false)}
							>
								Cancel
							</ClayButton>

							<ClayButton
								disabled={!selectedBlock}
								displayType="primary"
								onClick={() => {
									onOpenChange(false);

									dispatch({
										payload: {
											content: {},
											type: selectedBlock,
										} as any,
										type: SolutionTypes.SET_NEW_BLOCK,
									});
								}}
							>
								Save
							</ClayButton>
						</div>
					</ClayModal.Body>
				</ClayModal>
			)}
		</div>
	);
};

export default Details;
