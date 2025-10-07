/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import PropTypes from 'prop-types';
import React, {useEffect, useRef} from 'react';

function InfiniteScroller({
	children,
	customLoader: CustomLoader,
	maxHeight,
	onBottomTouched,
	scrollCompleted,
}) {
	const infiniteLoaderRef = useRef(null);
	const observerRef = useRef(null);
	const scrollingAreaRef = useRef(null);

	useEffect(() => {
		if (
			infiniteLoaderRef.current &&
			scrollingAreaRef.current &&
			!scrollCompleted
		) {
			const options = {
				root: scrollingAreaRef.current,
				threshold: 1,
			};

			observerRef.current = new IntersectionObserver((entries) => {
				if (entries[0].isIntersecting) {
					onBottomTouched();
				}
			}, options);

			observerRef.current.observe(infiniteLoaderRef.current);
		}

		return () => {
			observerRef.current?.disconnect();
		};
	}, [
		infiniteLoaderRef,
		observerRef,
		onBottomTouched,
		scrollCompleted,
		scrollingAreaRef,
	]);

	return (
		<div
			className="inline-scroller"
			ref={scrollingAreaRef}
			style={maxHeight ? {maxHeight} : null}
		>
			{children}

			{!scrollCompleted &&
				(CustomLoader ? (
					<CustomLoader ref={infiniteLoaderRef} />
				) : (
					<ClayLoadingIndicator ref={infiniteLoaderRef} small />
				))}
		</div>
	);
}

InfiniteScroller.propTypes = {
	customLoader: PropTypes.element,
	maxHeight: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
	onBottomTouched: PropTypes.func.isRequired,
	scrollCompleted: PropTypes.bool.isRequired,
};

export default InfiniteScroller;
