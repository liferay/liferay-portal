# Liferay Commerce

Liferay Commerce is an open source digital commerce platform written in Java. It was
built from the ground up to work with Liferay Portal so that Liferay developers
can leverage fully integrated web content management systems and best-in-breed
portal capabilities in their commerce projects.

Liferay designed it for commerce projects that are very large or complex;
it can support millions of products in challenging B2B industries.

## Key Features

* Catalog Management and Product Browsing
* Checkout, Discounts and Pricing
* Payments and Tax Management
* Shipping Management
* Order Management
* Customer Account Management
* Multicurrency and Multilanguage

## Quick Start

Requirements: [Liferay Portal 7.1 GA1](https://github.com/liferay/liferay-portal)

1.  Clone the Liferay Commerce repository (this
    [repository](https://github.com/liferay/com-liferay-commerce)) to the same
    level of your directory tree as your Liferay Home folder (Liferay Home is
    the folder that contains the Tomcat folder for Liferay 7.1).

    Alternatively, you can determine where Commerce will deploy by passing in
    a path to Liferay Home in your `gradle.properties` file. For example:
    `app.server.parent.dir=/home/computer/Documents/liferay-ce-7.1.0-ga1/`

1.  Start Liferay Portal 7.1.

    In the terminal, navigate to the source code folder
    (`com-liferay-commerce`).

    Windows Users: enter `gradlew.bat deploy -Dbuild.profile=portal`

    MacOS/Linux: enter `./gradlew deploy -Dbuild.profile=portal`

   This takes a few minutes.

1.  When the build is complete, run a full reindex of all search indexes. Go
    to--or refresh-- http://localhost:8080 in your browser. In the left-hand
    menu, go to *Control Panel* &rarr; *System* &rarr; *Search* and
    click *Execute* next to *Reindex all search indexes*.

Liferay Commerce's features are now available in your Portal instance.

## Bug Reporting

Did you find a bug? Please open a ticket for it at [issues.liferay.com](https://issues.liferay.com).

## Stay Connected

There are many ways for you to learn what's new in Liferay Commerce, get answers to
questions and connect with other Liferay community members.

### Twitter

Follow us on Twitter:

- [@Liferay](http://twitter.com/Liferay) Latest announcements
- [@LiferayDocs](http://twitter.com/Liferaydocs) New articles
- [@LiferayEng](http://twitter.com/Liferayeng) Tweets from the core engineering
team

### Blog

Read details on announcements, engage in discussions and learn more by following
[Liferay's Blog](http://www.liferay.com/community/blogs).

### Forum

Do you have questions? Ask them on our
[forums](https://community.liferay.com/forums/-/message_boards/category/110421633).

### Chat

Join the conversation in [`#commerce` channel](https://liferay-community.slack.com/messages/CBJBV8H8U) on Liferay's Community Chat.

## License

This project, *Liferay Commerce*, is free software ("Licensed Software"); you can
redistribute it and/or modify it under the terms of the [GNU Lesser General Public License](./LICENSE.txt)
as published by the Free Software Foundation; either version 2.1 of the License,
or (at your option) any later version.

This project is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; including but not limited to, the implied warranty of MERCHANTABILITY,
NONINFRINGEMENT, or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
Public License for more details.

You should have received a copy of the [GNU Lesser General Public License](./LICENSE.txt)
along with this project; if not, write to the Free Software Foundation, Inc., 51
Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA