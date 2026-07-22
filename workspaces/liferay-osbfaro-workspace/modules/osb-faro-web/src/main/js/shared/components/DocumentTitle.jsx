import {PropTypes} from 'prop-types';
import {useEffect} from 'react';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';
import {useParams} from 'react-router-dom';

const DocumentTitle = ({productName, title}) => {
	const {groupId = ''} = useParams();
	const LDPEnabled = useLDPEnabled({groupId});

	useEffect(() => {
		const defaultTitle =
			productName ||
			(LDPEnabled
				? Liferay.Language.get('liferay-data-platform')
				: Liferay.Language.get('analytics-cloud'));

		document.title = title ? `${title} - ${defaultTitle}` : defaultTitle;
	}, [LDPEnabled, productName, title]);

	return null;
};

DocumentTitle.propTypes = {
	productName: PropTypes.string,
	title: PropTypes.string
};

export default DocumentTitle;
