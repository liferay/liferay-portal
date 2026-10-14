import {useEffect, useState} from 'react';

interface IUseBodyWidthBelowOptions {
	inclusive?: boolean;
}

const isBodyWidthBelow = (width: number, inclusive: boolean) =>
	inclusive
		? document.body.clientWidth <= width
		: document.body.clientWidth < width;

export const useBodyWidthBelow = (
	width: number,
	{inclusive = false}: IUseBodyWidthBelowOptions = {}
) => {
	const [below, setBelow] = useState(() =>
		isBodyWidthBelow(width, inclusive)
	);

	useEffect(() => {
		const handleResize = () => setBelow(isBodyWidthBelow(width, inclusive));

		window.addEventListener('resize', handleResize);

		return () => window.removeEventListener('resize', handleResize);
	}, [inclusive, width]);

	return below;
};
