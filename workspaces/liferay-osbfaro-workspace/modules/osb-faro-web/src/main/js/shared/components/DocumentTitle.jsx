import {PropTypes} from 'prop-types';
import {useDocumentFavicon} from 'shared/hooks/useDocumentFavicon';
import {useEffect} from 'react';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';
import {useParams} from 'react-router-dom';

const DocumentTitle = ({ldpEnabled, productName, title}) => {
	const {groupId = ''} = useParams();
	const routeLDPEnabled = useLDPEnabled({groupId});

	const LDPEnabled = ldpEnabled ?? routeLDPEnabled;

	useDocumentFavicon(LDPEnabled);

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
	ldpEnabled: PropTypes.bool,
	productName: PropTypes.string,
	title: PropTypes.string
};

export default DocumentTitle;
