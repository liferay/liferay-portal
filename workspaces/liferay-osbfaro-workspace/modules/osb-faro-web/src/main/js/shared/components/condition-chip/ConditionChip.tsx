import ConditionSticker, {ConditionStickerType} from './ConditionSticker';
import {DragStates} from './useSortableChip';
import getCN from 'classnames';
import {clamp} from 'lodash';
import React, {useRef} from 'react';
import {ClayButtonWithIcon} from '@clayui/button';
import {mergeRef} from 'shared/util/util';
import {sub} from 'shared/util/lang';
import {Text} from '@clayui/core';

interface IConditionChipProps {
	dragState?: DragStates;
	handle?: React.ReactNode;
	label: React.ReactNode;
	name: string;
	onRemove?: () => void;
	onSelect?: () => void;
	overline?: React.ReactNode;
	selected?: boolean;
	staticSticker?: boolean;
	sticker: ConditionStickerType;
}

const CONTROL_SELECTOR = '[data-chip-control]';

const ConditionChip = React.forwardRef<HTMLDivElement, IConditionChipProps>(
	(
		{
			dragState,
			handle,
			label,
			name,
			onRemove,
			onSelect,
			overline,
			selected,
			staticSticker,
			sticker,
		},
		ref
	) => {
		const chipRef = useRef<HTMLDivElement>(null);

		const getControls = () =>
			Array.from(
				chipRef.current?.querySelectorAll<HTMLElement>(
					CONTROL_SELECTOR
				) ?? []
			);

		const handleKeyDown = (event: React.KeyboardEvent<HTMLDivElement>) => {
			if (event.defaultPrevented) {
				return;
			}

			const target = event.target as HTMLElement;

			if (target === chipRef.current) {
				if (onSelect && event.key === 'Enter') {
					event.preventDefault();

					onSelect();

					return;
				}

				if (event.key === ' ' || event.key === 'Enter') {
					event.preventDefault();

					getControls()[0]?.focus();
				}

				return;
			}

			if (!target.matches(CONTROL_SELECTOR)) {
				return;
			}

			if (event.key === 'Escape') {
				event.preventDefault();

				chipRef.current?.focus();

				return;
			}

			if (event.key === 'ArrowLeft' || event.key === 'ArrowRight') {
				event.preventDefault();

				const controls = getControls();

				const index =
					controls.indexOf(target) +
					(event.key === 'ArrowRight' ? 1 : -1);

				controls[clamp(index, 0, controls.length - 1)]?.focus();
			}
		};

		return (
			<div
				aria-current={selected ? 'true' : undefined}
				aria-label={name}
				className={getCN(
					'align-items-center condition-chip d-flex rounded-lg',
					{
						'condition-chip-draggable': !!handle,
						'condition-chip-selectable': !!onSelect,
						'condition-chip-selected': selected,
						'condition-chip-static-sticker': staticSticker,
						[`condition-chip-${dragState}`]: dragState,
					}
				)}
				onClick={onSelect}
				onKeyDown={handleKeyDown}
				ref={mergeRef(ref, chipRef)}
				role="group"
				tabIndex={0}
			>
				{handle}

				<div className="align-items-center condition-chip-content d-flex flex-grow-1 px-2 py-2">
					<ConditionSticker className="mr-2" {...sticker} />

					<span className="condition-chip-text d-flex flex-column text-break">
						{overline && (
							<span className="d-block text-uppercase">
								<Text
									color="secondary"
									size={1}
									weight="normal"
								>
									{overline}
								</Text>
							</span>
						)}

						<span className="d-block">
							<Text size={4} weight="semi-bold">
								{label}
							</Text>
						</span>
					</span>
				</div>

				{onRemove && (
					<ClayButtonWithIcon
						aria-label={
							sub(Liferay.Language.get('remove-x'), [
								name,
							]) as string
						}
						className="condition-chip-remove flex-shrink-0 mr-1"
						data-chip-control
						data-html2canvas-ignore
						displayType="unstyled"
						monospaced
						onClick={(event) => {
							event.stopPropagation();

							onRemove();
						}}
						size="sm"
						symbol="times-circle"
						tabIndex={-1}
						title={
							sub(Liferay.Language.get('remove-x'), [
								name,
							]) as string
						}
					/>
				)}
			</div>
		);
	}
);

export default ConditionChip;
