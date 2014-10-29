package org.techteam.bashhappens.gui.adapters;

import org.techteam.bashhappens.content.ContentFactory;

import java.util.ArrayList;
import java.util.List;

public class SectionsBuilder {
    public static interface Section {
        String getSideMenuText();
        String getActionBarText();
        ContentFactory.ContentSection getContentSection();
        boolean isDisabled();
    }

    public static class GroupSection implements Section {
        public GroupSection(String label) {
            this.label = label;
        }

        public String getSideMenuText() {
            return label;
        }

        @Override
        public String getActionBarText() {
            return null;
        }

        @Override
        public ContentFactory.ContentSection getContentSection() {
            return null;
        }

        @Override
        public boolean isDisabled() {
            return true;
        }

        private String label;
    }

    public static class SelectableSection implements Section {
        public SelectableSection(String label, GroupSection group, ContentFactory.ContentSection contentSection) {
            this.sideMenuText = label;
            this.actionBarText = group.getSideMenuText() + ":" + sideMenuText;
            this.contentSection = contentSection;
        }

        @Override
        public String getSideMenuText() {
            return sideMenuText;
        }

        @Override
        public String getActionBarText() {
            return actionBarText;
        }

        @Override
        public ContentFactory.ContentSection getContentSection() {
            return contentSection;
        }

        @Override
        public boolean isDisabled() {
            return false;
        }

        private String sideMenuText;
        private String actionBarText;
        private ContentFactory.ContentSection contentSection;
    }

    //TODO: fix enum values for ContentSection
    public static List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();

        //******************BASH.ORG************************
        GroupSection bashOrg = new GroupSection("Bash.Org");

        sections.add(bashOrg);
        sections.add(new SelectableSection(
                "Новые",
                bashOrg,
                ContentFactory.ContentSection.BASH_ORG_NEWEST));

        sections.add(new SelectableSection(
                "Случайные",
                bashOrg,
                ContentFactory.ContentSection.BASH_ORG_NEWEST));

        sections.add(new SelectableSection(
                "Лучшие",
                bashOrg,
                ContentFactory.ContentSection.BASH_ORG_NEWEST));

        sections.add(new SelectableSection(
                "По рейтингу",
                bashOrg,
                ContentFactory.ContentSection.BASH_ORG_NEWEST));

        sections.add(new SelectableSection(
                "Бездна",
                bashOrg,
                ContentFactory.ContentSection.BASH_ORG_NEWEST));

        sections.add(new SelectableSection(
                "Топ бездны",
                bashOrg,
                ContentFactory.ContentSection.BASH_ORG_NEWEST));

        sections.add(new SelectableSection(
                "Лучшие бездны",
                bashOrg,
                ContentFactory.ContentSection.BASH_ORG_NEWEST));


        //*********************ITHAPPENS************************
        GroupSection itHappens = new GroupSection("IT Happens");

        sections.add(itHappens);
        sections.add(new SelectableSection(
                "Свежие",
                bashOrg,
                ContentFactory.ContentSection.IT_HAPPENS_NEWEST));

        sections.add(new SelectableSection(
                "Лучшие",
                bashOrg,
                ContentFactory.ContentSection.IT_HAPPENS_NEWEST));

        sections.add(new SelectableSection(
                "Случайные",
                bashOrg,
                ContentFactory.ContentSection.IT_HAPPENS_NEWEST));

        return sections;
    }
}
