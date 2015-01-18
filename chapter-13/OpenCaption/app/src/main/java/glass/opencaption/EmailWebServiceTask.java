package glass.opencaption;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;

// START:asynctask
public class EmailWebServiceTask extends AsyncTask<String, Void, Void> {
    // END:asynctask
    private AmazonSimpleEmailServiceClient sesClient;
    private String toAddress;
    private String fromVerifiedAddress;
    // START:asynctask
    public EmailWebServiceTask( Context context ) {
        String accessKey = context.getString(R.string.aws_access_key);
        String secretKey = context.getString(R.string.aws_secret_key);
        fromVerifiedAddress = context.getString(R.string.aws_verified_address);
        toAddress = getAccountEmail( context );
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        sesClient = new AmazonSimpleEmailServiceClient( credentials );
    }
    protected Void doInBackground(String...messages) {
        if( messages.length == 0 ) return null;
        // build the message and destination objects
        Content subject = new Content( "OpenCaption" );
        Body body = new Body( new Content( messages[0] ) );
        Message message = new Message( subject, body );
        Destination destination = new Destination().withToAddresses( toAddress );
        // send out the email
        SendEmailRequest request =
                new SendEmailRequest( fromVerifiedAddress, destination, message );
        // END:asynctask
        SendEmailResult result =
                // START:asynctask
                sesClient.sendEmail( request );
        // END:asynctask
        Log.d( "glass.opencaption", "AWS SES resp message id:" + result.getMessageId() );
        // START:asynctask
        return null;
    }
    // END:asynctask
    // START:accountEmail
    private String getAccountEmail( Context context ) {
        Account[] accounts =
                AccountManager.get(context).getAccountsByType("com.google");
        Account account = accounts.length > 0 ? accounts[0] : null;
        // END:accountEmail
        Log.i("glass.opencaption", account.name );
        // START:accountEmail
        return account.name;
    }
    // END:accountEmail
}