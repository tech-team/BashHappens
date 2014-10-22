package org.techteam.bashhappens.content.bashorg;

import org.jsoup.nodes.Element;

public class BashOrgEntry {
    public static final String DOM_CLASS_NAME = "quote";
    private static final String ACTIONS_BAR_CLASS = "actions";
    private static final String TEXT_CLASS = "text";

    private String id;
    private String creationDate;
    private String text;

    public BashOrgEntry(String id, String creationDate, String text) {
        this.id = id;
        this.creationDate = creationDate;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getText() {
        return text;
    }

    public static BashOrgEntry fromHtml(Element element) {
        Element actionsBar;
        String text;
        try {
            actionsBar = element.getElementsByClass(ACTIONS_BAR_CLASS).get(0);
            text = element.getElementsByClass(TEXT_CLASS).get(0).text();
        } catch (Exception ignored) {
            return null;
        }

        String id = actionsBar.getElementsByClass("id").get(0).text().substring(1);
        String creationDate = actionsBar.getElementsByClass("date").text();

        return new BashOrgEntry(id, creationDate, text);
    }
}
