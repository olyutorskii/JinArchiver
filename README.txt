[UTF-8 Japanese]

                               JinArchiver
                                  README

                                              Copyright(c) 2009 olyutorskii


=== JinArchiverとは ===


 JinArchiverは、人狼BBSの過去ログを独自のXML形式でローカルディスクに
保存するためのツールです。

※ このアーカイブにはJindolfの実行バイナリは含まれていません。
　 Jindolfを動かしたい方は、jindolfで始まり拡張子が*.jarであるファイルを
　 別途入手してください。
※ 人狼BBSのURLは [ http://homepage2.nifty.com/ninjinia/ ] まで
※ 人狼BBSを主催するninjin氏は、JinArchiverの製作に一切関与していません。
　 JinArchiverに関する問い合わせををninjin氏へ投げかけないように！約束だよ！


=== 使い方 ===

例) ※ F国 1507村 のアーカイブをディレクトリ/tmpに作りたい場合。

java -jar jinarchiver-X.X.X.jar -land wolff -vid 1507 -outdir /tmp

オプション詳細は-helpオプションで確認してください。


=== ソースコードに関して ===

 - JinArchiverはJava言語(JLS3)で記述されたプログラムです。
 - JinArchiverはJRE1.5に準拠したJava実行環境で利用できるように作られています。
   原則として、JRE1.5に準拠した実行系であれば、プラットフォームを選びません。


=== アーカイブ管理体制 ===

  このアーカイブは、UTF-8による開発環境を前提として構成されています。
  このアーカイブの原本となる開発資産は、
      http://hg.sourceforge.jp/view/jindolf/JinArchiver/
  を上位に持つMercurialリポジトリで管理されています。
  アーカイブの代わりにMercurialを通じて開発資産にアクセスすることにより、
  任意の文字コードに変換されたJavaソースファイルや各種リソースを
  容易に入手することが可能です。


=== 開発プロジェクト運営元 ===

  http://sourceforge.jp/projects/jindolf/ まで。


=== ディレクトリ内訳構成 ===

./README.txt
    あなたが今見てるこれ。

./CHANGELOG.txt
    変更履歴。

./LICENSE.txt
    ライセンスに関して。

./src/
    Javaのソースコード。XMLなどの各種リソース。

./test/
    JUnit 4.* 用のテストコード。

./pom.xml
    Maven2用プロジェクト構成定義ファイル。

./build.xml
    Ant用追加タスク。

./src/main/java/
    Javaのソースコード。

./src/main/resources/
    プロパティファイルなどの各種リソース。

./src/test/java/
    JUnit 4.* 用のユニットテストコード。

./src/main/config/checks.xml
    Checkstyle用configファイル。

./src/main/config/pmdrules.xml
    PMD用ルール定義ファイル。

./src/main/assembly/descriptor.xml
    ソースアーカイブ構成定義ファイル。


--- EOF ---
