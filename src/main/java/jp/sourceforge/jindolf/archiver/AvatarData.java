/*
 * avatar model
 *
 * License : The MIT License
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jp.sourceforge.jindolf.corelib.PreDefAvatar;
import org.xml.sax.SAXException;

/**
 * Avatarモデル。
 */
public class AvatarData{

    private static final List<PreDefAvatar> PREDEF_AVATAR_LIST;

    static{
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder builder = factory.newDocumentBuilder();
            PREDEF_AVATAR_LIST = PreDefAvatar.buildPreDefAvatarList(builder);
        }catch(ParserConfigurationException e){
            throw new ExceptionInInitializerError(e);
        }catch(IOException e){
            throw new ExceptionInInitializerError(e);
        }catch(SAXException e){
            throw new ExceptionInInitializerError(e);
        }
    }


    private String fullName;
    private String shortName;
    private String avatarId;
    private String faceIconUri;


    /**
     * コンストラクタ。
     */
    public AvatarData(){
        super();
        return;
    }

    /**
     * コンストラクタ。
     * @param predefAvatar プリセット済みAvatar
     */
    public AvatarData(PreDefAvatar predefAvatar){
        this();

        this.fullName = predefAvatar.getFullName();
        this.shortName = predefAvatar.getShortName();
        this.avatarId = predefAvatar.getAvatarId();
        this.faceIconUri = null;

        return;
    }


    /**
     * プリセット済みAvatarをフルネームを用いて取得する。
     * @param seq フルネーム
     * @return 見つかったプリセット済みAvatar。見つからなければnull。
     */
    public static PreDefAvatar getPreDefAvatar(CharSequence seq){
        for(PreDefAvatar avatar : PREDEF_AVATAR_LIST){
            String fullName = avatar.getFullName();
            if(fullName.contentEquals(seq)){
                return avatar;
            }
        }
        return null;
    }

    /**
     * フルネームを取得する。
     * @return フルネーム
     */
    public String getFullName(){
        return this.fullName;
    }

    /**
     * フルネームを設定する。
     * @param fullName フルネーム
     */
    public void setFullName(String fullName){
        this.fullName = fullName;
        return;
    }

    /**
     * 短縮名を取得する。
     * @return 短縮名
     */
    public String getShortName(){
        return this.shortName;
    }

    /**
     * 短縮名を設定する。
     * @param shortName 短縮名
     */
    public void setShortName(String shortName){
        this.shortName = shortName;
        return;
    }

    /**
     * Avatar識別子を取得する。
     * @return Avatar識別子
     */
    public String getAvatarId(){
        return this.avatarId;
    }

    /**
     * Avatar識別子を設定する。
     * @param avatarId Avatar識別子
     */
    public void setAvatarId(String avatarId){
        this.avatarId = avatarId;
        return;
    }

    /**
     * 顔アイコンURI文字列を取得する。
     * @return 顔アイコンURI文字列
     */
    public String getFaceIconUri(){
        return this.faceIconUri;
    }

    /**
     * 顔アイコンURI文字列を設定する。
     * @param faceIconUri 顔アイコンURI文字列
     */
    public void setFaceIconUri(String faceIconUri){
        this.faceIconUri = faceIconUri;
        return;
    }

    /**
     * avatar要素をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpXml(Writer writer) throws IOException{
        writer.append("<avatar\n");

        XmlUtils.indent(writer, 1);
        XmlUtils.attrOut(writer, "avatarId", this.avatarId);
        writer.append('\n');

        XmlUtils.indent(writer, 1);
        XmlUtils.attrOut(writer, "fullName", this.fullName);

        writer.append(' ');
        XmlUtils.attrOut(writer, "shortName", this.shortName);
        writer.append('\n');

        if(this.faceIconUri != null){
            XmlUtils.indent(writer, 1);
            XmlUtils.attrOut(writer, "faceIconURI", this.faceIconUri);
            writer.append('\n');
            // F1014対策
        }

        writer.append("/>\n");
        writer.flush();

        return;
    }

}
