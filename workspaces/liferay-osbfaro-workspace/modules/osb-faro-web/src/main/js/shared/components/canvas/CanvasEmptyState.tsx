import emptyStateReducedMotionURL from '../../../../images/states/empty_state_reduced_motion.svg?url';
import emptyStateURL from '../../../../images/states/empty_state.svg?url';
import getCN from 'classnames';
import React, {useSyncExternalStore} from 'react';
import {Heading, Text} from '@clayui/core';

export type DropState = 'dragging' | 'idle' | 'over';

export interface ICanvasEmptyStateProps
	extends React.HTMLAttributes<HTMLDivElement> {
	description: string;
	dropState?: DropState;
	title: string;
}

const REDUCED_MOTION_CLASS_NAME = 'c-prefers-reduced-motion';

const isReducedMotion = () =>
	document.body.classList.contains(REDUCED_MOTION_CLASS_NAME);

/**
 * The accessibility menu toggles the reduced motion class on the body at
 * runtime, so it is observed rather than read once.
 */
const subscribeToReducedMotion = (onChange: () => void) => {
	const observer = new MutationObserver(onChange);

	observer.observe(document.body, {
		attributeFilter: ['class'],
		attributes: true,
	});

	return () => observer.disconnect();
};

/**
 * Mirrors the markup of ClayEmptyState, whose title only accepts a string
 * rendered inside a span, so that the headline can be a Clay Heading. While an
 * item is dragged, the content is hidden rather than removed so that the drop
 * area keeps its height.
 */
const CanvasEmptyState: React.FC<ICanvasEmptyStateProps> = ({
	className,
	description,
	dropState = 'idle',
	title,
	...otherProps
}) => {
	const reducedMotion = useSyncExternalStore(
		subscribeToReducedMotion,
		isReducedMotion
	);

	return (
		<div
			className={getCN(
				'align-items-center canvas-empty-state d-flex justify-content-center rounded-lg',
				`canvas-empty-state-${dropState}`,
				{

					// The utility is important, so it would hide the drop color

					'bg-white': dropState !== 'over',
				},
				className
			)}
			{...otherProps}
		>
			<div
				className={getCN('c-empty-state c-empty-state-animation', {
					invisible: dropState !== 'idle',
				})}
			>
				<div className="c-empty-state-image">
					<div className="c-empty-state-aspect-ratio">
						<img
							alt=""
							className="aspect-ratio-item aspect-ratio-item-fluid"
							src={
								reducedMotion
									? emptyStateReducedMotionURL
									: emptyStateURL
							}
						/>
					</div>
				</div>

				<div className="c-empty-state-title text-dark">
					<Heading fontSize={6} level={3} weight="bold">
						{title}
					</Heading>
				</div>

				<div className="c-empty-state-text">
					<Text color="secondary">{description}</Text>
				</div>
			</div>
		</div>
	);
};

export default CanvasEmptyState;
