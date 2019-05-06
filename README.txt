[UTF-8 Japanese]

                           J i n A r c h i v e r
                                  README

                                              Copyright(c) 2009 olyutorskii


=== JinArchiverとは ===


 JinArchiverは、人狼BBSの過去ログを独自のXML形式でローカルディスクに
保存するためのツールです。

※ 人狼BBSのURLは [ http://ninjinix.com/ ] まで
※ 人狼BBSを主催するninjin氏は、JinArchiverの製作に一切関与していません。
　 JinArchiverに関する問い合わせををninjin氏へ投げかけないように！約束だよ！


=== 使い方 ===

例) ※ F国 1507村 のアーカイブをディレクトリ/tmpに作りたい場合。

java -jar jinarchiver-X.X.X.jar -land wolff -vid 1507 -outdir /tmp

オプション詳細は-helpオプションで確認してください。


=== ソースコードに関して ===

 - JinArchiverはJava言語(JavaSE8)で記述されたプログラムです。
 - JinArchiverはJavaSE8に準拠したJava実行環境で利用できるように作られています。
   原則として、JavaSE8に準拠した実行系であれば、プラットフォームを選びません。


=== 開発プロジェクト運営元 ===

  https://ja.osdn.net/projects/jindolf/ まで。


=== ディレクトリ内訳構成 ===

基本的にはMaven3のmaven-archetype-quickstart構成に準じます。

./README.txt
    あなたが今見てるこれ。

./CHANGELOG.txt
    変更履歴。

./LICENSE.txt
    ライセンスに関して。

./pom.xml
    Maven3用プロジェクト構成定義ファイル。

./checkstyle.xml
    Checkstyle用configファイル。

./pmdrules.xml
    PMD用ルール定義ファイル。

./src/assembly/src.xml
    ソースアーカイブ構成定義ファイル。

./src/main/java/
    Javaのソースコード。

./src/main/resources/
    プロパティファイルなどの各種リソース。

./src/test/java/
    JUnit 4.* 用のユニットテストコード。


--- EOF ---
