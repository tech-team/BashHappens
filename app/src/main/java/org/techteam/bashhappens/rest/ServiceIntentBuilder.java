package org.techteam.bashhappens.rest;

import android.content.Context;
import android.content.Intent;

import org.techteam.bashhappens.content.ContentSource;

public final class ServiceIntentBuilder {

    public static Intent getPostsIntent(Context context, String requestId, ContentSource contentSource, int loadIntention) {
        Intent intent = new Intent(context, BHService.class);
        intent.putExtra(BHService.IntentExtras.REQUEST_ID, requestId);
        intent.putExtra(BHService.IntentExtras.OPERATION, BHService.IntentExtras.GetPostsOperation.TAG);

        intent.putExtra(BHService.IntentExtras.GetPostsOperation.CONTENT_SOURCE, contentSource);
        intent.putExtra(BHService.IntentExtras.GetPostsOperation.LOAD_INTENTION, loadIntention);
        return intent;
    }

    public static Intent voteBashIntent(Context context, String requestId, int entryPosition, String entryId, String rating, int direction) {
        Intent intent = new Intent(context, BHService.class);
        intent.putExtra(BHService.IntentExtras.REQUEST_ID, requestId);
        intent.putExtra(BHService.IntentExtras.OPERATION, BHService.IntentExtras.BashVoteOperation.TAG);

        intent.putExtra(BHService.IntentExtras.BashVoteOperation.ENTRY_POSITION, entryPosition);
        intent.putExtra(BHService.IntentExtras.BashVoteOperation.ENTRY_ID, entryId);
        intent.putExtra(BHService.IntentExtras.BashVoteOperation.RATING, rating);
        intent.putExtra(BHService.IntentExtras.BashVoteOperation.DIRECTION, direction);
        return intent;
    }

    public static Intent voteBayanBashIntent(Context context, String requestId, int entryPosition, String entryId) {
        Intent intent = new Intent(context, BHService.class);
        intent.putExtra(BHService.IntentExtras.REQUEST_ID, requestId);
        intent.putExtra(BHService.IntentExtras.OPERATION, BHService.IntentExtras.BashVoteOperation.TAG);

        intent.putExtra(BHService.IntentExtras.BashVoteOperation.ENTRY_POSITION, entryPosition);
        intent.putExtra(BHService.IntentExtras.BashVoteOperation.ENTRY_ID, entryId);
        intent.putExtra(BHService.IntentExtras.BashVoteOperation.BAYAN, true);
        return intent;
    }

}