import { AdvancedMarker, Pin, useAdvancedMarkerRef, InfoWindow } from '@vis.gl/react-google-maps';


const AdvancedMarkerWithInfoWindow = (
	{
		distributor,
		hoveredMarker,
		onClick,
		onClose,
		onMouseOut,
		onMouseOver,
		promoCodeStore
	}
) => {
	const [markerRef, marker] = useAdvancedMarkerRef();

	return (
		<AdvancedMarker
			key={`advancedMarker_${distributor.id}`}
			onClick={() => onClick(distributor.id)}
			onMouseEnter={() => onMouseOver(distributor.id)}
			onMouseLeave={() => onMouseOut()}
			position={distributor.position}
			ref={markerRef}
			title={distributor.name}
		>
			{
				distributor.name === promoCodeStore ?
					<Pin background={'#4285F4'} borderColor={'#1A73E8'} glyphColor={'#FFF'} /> :
					<Pin background={'#f30808ff'} borderColor={'#1A73E8'} glyphColor={'#FFF'} />
			}
			{
				hoveredMarker == distributor.id &&
				(
					<InfoWindow
						anchor={marker}
						headerContent={(<strong>{distributor.name}</strong>)}
						key={`infoWindow_${distributor.id}`}
						onCloseClick={() => onClose(null)}
						position={distributor.position}
					>
						<div style={{ maxWidth: 300 }}>
							<div>{distributor.street}</div>
							<div>{distributor.city}, {distributor.state}, {distributor.zipCode}</div>
						</div>
					</InfoWindow>
				)
			}
		</AdvancedMarker>
	);

};

export default AdvancedMarkerWithInfoWindow;