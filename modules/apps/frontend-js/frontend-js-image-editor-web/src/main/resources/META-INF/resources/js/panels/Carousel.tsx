/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import classNames from 'classnames';
import React, {useCallback, useEffect, useRef, useState} from 'react';

interface Props extends React.HTMLAttributes<HTMLDivElement> {
	children: React.ReactNode;

	className: string;

	itemCount: number;
}

export function Carousel({
	children,
	className,
	itemCount,
	...trackProps
}: Props) {
	const trackRef = useRef<HTMLDivElement>(null);

	const [scroll, setScroll] = useState({
		left: false,
		overflows: false,
		right: false,
	});

	const updateScroll = useCallback(() => {
		const element = trackRef.current;

		if (!element) {
			return;
		}

		const overflows =
			getComputedStyle(element).overflowX === 'auto' &&
			element.scrollWidth > element.clientWidth + 4;

		const left = element.scrollLeft > 4;
		const right =
			element.scrollLeft + element.clientWidth < element.scrollWidth - 4;

		setScroll((current) =>
			current.left === left &&
			current.overflows === overflows &&
			current.right === right
				? current
				: {left, overflows, right}
		);
	}, []);

	useEffect(() => {
		updateScroll();

		const observer = new ResizeObserver(updateScroll);

		if (trackRef.current) {
			observer.observe(trackRef.current);
		}

		return () => observer.disconnect();
	}, [itemCount, updateScroll]);

	const scrollByPage = (direction: -1 | 1) => {
		const element = trackRef.current;

		element?.scrollBy({
			behavior: prefersReducedMotion() ? 'auto' : 'smooth',
			left: direction * element.clientWidth * 0.8,
		});
	};

	return (
		<div className="editor-carousel">
			{scroll.overflows && (
				<ClayButtonWithIcon
					aria-hidden="true"
					aria-label={Liferay.Language.get('previous')}
					borderless
					className="editor-carousel-arrow"
					disabled={!scroll.left}
					displayType="secondary"
					onClick={() => scrollByPage(-1)}
					size="sm"
					symbol="angle-left"
					tabIndex={-1}
				/>
			)}

			<div
				{...trackProps}
				className={classNames('editor-carousel-track', className)}
				onScroll={updateScroll}
				ref={trackRef}
			>
				{children}
			</div>

			{scroll.overflows && (
				<ClayButtonWithIcon
					aria-hidden="true"
					aria-label={Liferay.Language.get('next')}
					borderless
					className="editor-carousel-arrow"
					disabled={!scroll.right}
					displayType="secondary"
					onClick={() => scrollByPage(1)}
					size="sm"
					symbol="angle-right"
					tabIndex={-1}
				/>
			)}
		</div>
	);
}

function prefersReducedMotion(): boolean {
	return (
		document.body.classList.contains('c-prefers-reduced-motion') ||
		(!!window.matchMedia &&
			window.matchMedia('(prefers-reduced-motion: reduce)').matches)
	);
}
