/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useState} from 'react';
import {Card} from '../../../../../../../frontend-data-set-web/src/main/resources/META-INF/resources/views/cards/Cards';
import './carousel.scss';

const CarouselView = (props: any) => {
	const initialItems = Array.from({ length: 16 }, (_, index) => ({
		url: `https://via.placeholder.com/800x500?text=Imagen+${index + 1}`,
		name: `Imagen ${index + 1}`,
		size: '800x500',
	  }));
	
	  const schema = {
		  description: 'description',
		  image: 'image',
		  link: 'link',
		  sticker: 'sticker',
		  symbol: 'symbol',
		  title: 'title',
		};
  
	  const [selectedIndex, setSelectedIndex] = useState(0);
	
	  const thumbnailsToShow = 5;
	  const startIndex = Math.max(
		0,
		Math.min(
		  selectedIndex - Math.floor(thumbnailsToShow / 2),
		  initialItems.length - thumbnailsToShow
		)
	  );
	
	  const handleThumbnailClick = (index: number) => {
		setSelectedIndex(index);
	  };
	
	  const handlePrevClick = () => {
		const newIndex =
		  selectedIndex === 0 ? initialItems.length - 1 : selectedIndex - 1;
		setSelectedIndex(newIndex);
	  };
	
	  const handleNextClick = () => {
		const newIndex =
		  selectedIndex === initialItems.length - 1 ? 0 : selectedIndex + 1;
		setSelectedIndex(newIndex);
	  };
	
	  const { url, name, size } = initialItems[selectedIndex];
	  const visibleThumbnails = initialItems.slice(
		startIndex,
		startIndex + thumbnailsToShow
	  );
	
	  return (
		<div className='carousel'>
		  <div className='main-image-container card'>
			<div>
			  <img src={url} alt={name} className='main-image' />
			  <div className='card-info'>
				<p>{name}</p>
				<p>{size}</p>
			  </div>
			</div>
		  </div>
	
		  <div className='thumbnail-wrapper'>
			<div className='nav-buttons'>
			  <button className='nav-button' onClick={handlePrevClick}>
				{'<'}
			  </button>
			  <button className='nav-button' onClick={handleNextClick}>
				{'>'}
			  </button>
			</div>
			<div className='thumbnail-container'>
			  {visibleThumbnails.map((image, index) => {
				const actualIndex = startIndex + index;
				return (
				  <div
					key={actualIndex}
					className={`thumbnail card ${
					  actualIndex === selectedIndex ? 'selected' : ''
					}`}
					onClick={() => handleThumbnailClick(actualIndex)}
				  >
					  <Card item={image} schema={schema}/>
				  </div>
				);
			  })}
			</div>
		  </div>
		</div>
	  );
};

export default CarouselView;