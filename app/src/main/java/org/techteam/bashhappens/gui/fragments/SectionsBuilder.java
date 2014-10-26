package org.techteam.bashhappens.gui.fragments;

import org.techteam.bashhappens.content.ContentFactory;

import java.util.ArrayList;
import java.util.List;

public class SectionsBuilder {
    public class Section {
        public Section(String shortLabel, String fullLabel, ContentFactory.ContentSection contentSection) {
            this.shortLabel = shortLabel;
            this.fullLabel = fullLabel;
            this.contentSection = contentSection;
        }

        public String shortLabel;
        public String fullLabel;
        public ContentFactory.ContentSection contentSection;
    }

    public static List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();
        //sections.add(new Section());

        return sections;
    }
}
