import Body from './Body';
import Context from './Context';
import DocumentTitle from 'shared/components/DocumentTitle';
import getCN from 'classnames';
import Header from './Header';
import MaintenanceAlert from 'shared/components/MaintenanceAlert';
import React, {useRef} from 'react';
import Row from './Row';
import Sidebar, {SidebarContainerContext} from './Sidebar';
import SubHeader from './SubHeader';

interface IBasePageProps extends React.HTMLAttributes<HTMLElement> {
	documentTitle: string;
	fluid?: boolean;
}

export const BasePage: React.FC<IBasePageProps> & {
	Body: typeof Body;
	Context: typeof Context;
	Header: typeof Header;
	Row: typeof Row;
	Sidebar: typeof Sidebar;
	SubHeader: typeof SubHeader;
} = ({children, className, documentTitle}) => {
	const rootRef = useRef<HTMLDivElement>(null);

	return (
		<div className={getCN('index-root', className)} ref={rootRef}>
			<DocumentTitle title={documentTitle} />

			<MaintenanceAlert stripe />

			<SidebarContainerContext.Provider value={rootRef}>
				{children}
			</SidebarContainerContext.Provider>
		</div>
	);
};

BasePage.Body = Body;
BasePage.Context = Context;
BasePage.Header = Header;
BasePage.Row = Row;
BasePage.Sidebar = Sidebar;
BasePage.SubHeader = SubHeader;

export default BasePage;
