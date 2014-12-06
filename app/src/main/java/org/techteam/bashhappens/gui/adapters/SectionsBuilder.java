package org.techteam.bashhappens.gui.adapters;

import android.content.Context;

import org.techteam.bashhappens.R;
import org.techteam.bashhappens.content.ContentSection;

import java.util.ArrayList;
import java.util.List;

public class SectionsBuilder {
    public static interface Section {
        String getSideMenuText();
        String getActionBarText();
        ContentSection getContentSection();
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
        public ContentSection getContentSection() {
            return null;
        }

        @Override
        public boolean isDisabled() {
            return true;
        }

        private String label;
    }

    public static class SelectableSection implements Section {
        public SelectableSection(String label, GroupSection group, ContentSection contentSection) {
            this.sideMenuText = label;
            this.actionBarText = group.getSideMenuText() + ": " + sideMenuText;
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
        public ContentSection getContentSection() {
            return contentSection;
        }

        @Override
        public boolean isDisabled() {
            return false;
        }

        private String sideMenuText;
        private String actionBarText;
        private ContentSection contentSection;
    }

    //TODO: fix enum values for ContentSection
    public static List<Section> getSections(Context context) {
        List<Section> sections = new ArrayList<Section>();

        //******************BASH.ORG************************
        GroupSection bashOrg = new GroupSection(context.getString(R.string.bash_org_section));

        sections.add(bashOrg);
        sections.add(new SelectableSection(
                context.getString(R.string.bash_org_new_section),
                bashOrg,
                ContentSection.BASH_ORG_NEWEST));

        sections.add(new SelectableSection(
                context.getString(R.string.bash_org_random_section),
                bashOrg,
                ContentSection.BASH_ORG_NEWEST));

        sections.add(new SelectableSection(
                context.getString(R.string.bash_org_best_section),
                bashOrg,
                ContentSection.BASH_ORG_NEWEST));

        sections.add(new SelectableSection(
                context.getString(R.string.bash_org_by_rating_section),
                bashOrg,
                ContentSection.BASH_ORG_NEWEST));

        sections.add(new SelectableSection(
                context.getString(R.string.bash_org_abyss_section),
                bashOrg,
                ContentSection.BASH_ORG_NEWEST));

        sections.add(new SelectableSection(
                context.getString(R.string.bash_org_abyss_top_section),
                bashOrg,
                ContentSection.BASH_ORG_NEWEST));

        sections.add(new SelectableSection(
                context.getString(R.string.bash_org_abyys_top_section),
                bashOrg,
                ContentSection.BASH_ORG_NEWEST));


        //*********************IT HAPPENS***********************
        GroupSection itHappens = new GroupSection(context.getString(R.string.it_happens_section));

        sections.add(itHappens);
        sections.add(new SelectableSection(
                context.getString(R.string.it_happens_fresh_section),
                bashOrg,
                ContentSection.IT_HAPPENS_NEWEST));

        sections.add(new SelectableSection(
                context.getString(R.string.it_happens_best_section),
                bashOrg,
                ContentSection.IT_HAPPENS_NEWEST));

        sections.add(new SelectableSection(
                context.getString(R.string.it_happens_random_section),
                bashOrg,
                ContentSection.IT_HAPPENS_NEWEST));

        return sections;
    }

    public static int getDefaultSectionId() {
        return 1;
    }
}
