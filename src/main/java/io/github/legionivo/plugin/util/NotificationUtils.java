package io.github.legionivo.plugin.util;

import com.intellij.notification.*;
import com.intellij.psi.PsiMethod;
import io.github.legionivo.plugin.Settings;

public class NotificationUtils {

    public static void showSuccessfulNotification(PsiMethod method, String caseId) {
        String url = Settings.getInstance(method.getProject()).getApiUrl() + "index.php?/cases/view/" + caseId;
        Notification notification = new Notification(
                "TestRail.Action",
                "Export to TestRail",
                "Test " + "[" + method.getName() + "] successfully exported",
                NotificationType.INFORMATION,
                new NotificationListener.UrlOpeningListener(false)
        ).setImportant(true)
                .addAction(
                        new BrowseNotificationAction("Open testcase", url));
        Notifications.Bus.notify(notification);
    }

    public static void showFeatureAnnotationNotFoundNotification() {
        Notifications.Bus.notify(new Notification("TestRail.Action",
                "Export to TestRail",
                "@Feature annotation was not found on a class/test level",
                NotificationType.ERROR));
    }

    public static void showDisplayNameNotFoundNotification() {
        Notifications.Bus.notify(new Notification("TestRail.Action",
                "Export to TestRail",
                "@DisplayName annotation was not found on a test level",
                NotificationType.ERROR));
    }

    public static void showMethodErrorNotification(String method) {
        Notifications.Bus.notify(new Notification("TestRail.Action",
                "Export to TestRail",
                "Exception in " + "[" + method + "]. Test case export failed",
                NotificationType.ERROR));
    }
}
