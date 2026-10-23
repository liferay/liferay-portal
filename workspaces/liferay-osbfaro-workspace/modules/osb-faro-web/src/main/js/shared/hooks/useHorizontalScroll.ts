import {useCallback, useEffect, useRef, useState} from 'react';

export const useHorizontalScroll = (itemCount: number) => {
	const trackRef = useRef<HTMLDivElement>(null);

	const [canScrollNext, setCanScrollNext] = useState(false);
	const [canScrollPrevious, setCanScrollPrevious] = useState(false);

	const updateArrows = useCallback(() => {
		const track = trackRef.current;

		if (!track) {
			return;
		}

		const {clientWidth, scrollLeft, scrollWidth} = track;

		setCanScrollPrevious(scrollLeft > 0);
		setCanScrollNext(scrollLeft + clientWidth < scrollWidth - 1);
	}, []);

	useEffect(() => {
		updateArrows();

		window.addEventListener('resize', updateArrows);

		return () => window.removeEventListener('resize', updateArrows);
	}, [itemCount, updateArrows]);

	const scrollByItem = (direction: 1 | -1) => {
		const track = trackRef.current;

		const item = track?.firstElementChild as HTMLElement | null;

		if (!track || !item) {
			return;
		}

		const gap = parseFloat(getComputedStyle(track).columnGap) || 0;

		track.scrollBy({
			behavior: 'smooth',
			left: direction * (item.offsetWidth + gap),
		});
	};

	return {
		canScrollNext,
		canScrollPrevious,
		scrollNext: () => scrollByItem(1),
		scrollPrevious: () => scrollByItem(-1),
		trackRef,
		updateArrows,
	};
};
