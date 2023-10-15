# JinArchiver #

-----------------------------------------------------------------------


## JinArchiverとは ? ##

* JinArchiverは、[人狼BBS][BBS]の過去ログを独自のXML形式でローカルディスクに
保存するためにJavaで作られたツールです。

* ローカルに保存されたXMLファイルを[Jindolf][JINDOLF]で読み込み、
GUIを用いて閲覧することができます。

* Jindolf is a chat game browser application for 人狼BBS.
人狼BBS and Jindolf players belonged to the Japanese-speaking community.
Therefore, Jindolf documents and comments are heavily written in Japanese.

* JinArchiverは2023年10月頃まで [OSDN][OSDN](旧称 SourceForge.jp)
でホスティングされていました。
OSDNの可用性に関する問題が長期化しているため、GitHubへと移転してきました。


## ビルド方法 ##

* JinArchiverはビルドに際して [Maven 3.3.9+](https://maven.apache.org/)
と JDK 1.8+ を要求します。

* JinArchiverはビルドに際してJinParserなどのライブラリを必要とします。
開発時はMaven等を用いてこれらのライブラリを用意してください。

* Mavenを使わずとも `src/main/java/` 配下のソースツリーをコンパイルすることで
ライブラリを構成することが可能です。


## 使い方 ##

例) ※ F国 1507村 のアーカイブをディレクトリ/tmpに作りたい場合。

`java -jar jinarchiver-X.X.X.jar -land wolff -vid 1507 -outdir /tmp`

オプション詳細は-helpオプションで確認してください。


## ライセンス ##

* JinArchiverで開発されたソフトウェア資産には [The MIT License][MIT] が適用されます.


## プロジェクト創設者 ##

* 2009年に [Olyutorskii](https://github.com/olyutorskii) によってプロジェクトが発足しました。


## 実行環境 ##

* JinArchiverはJDK8に相当するJava実行環境で利用できるように作られています。
* JinArchiverはJindolfとは異なり、CLIアプリとして動作します。
* JinArchiverが人狼BBSサーバとHTTP通信を行う場合、TCP/IPネットワーク環境を
必要とします。


[JINDOLF]: https://github.com/olyutorskii/Jindolf
[BBS]: http://ninjinix.com/
[OSDN]: https://ja.osdn.net/projects/jindolf/scm/git/Jindolf/
[MIT]: https://opensource.org/licenses/MIT


--- EOF ---
