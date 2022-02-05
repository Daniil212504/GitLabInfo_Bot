package telegramBot;

import org.quartz.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import telegramBot.handlers.ExceptionHandler;
import telegramBot.jobs.JobsScheduler;
import telegramBot.jobs.ReadyToReviewMr_Ntf;
import telegramBot.utils.Property;

import static telegramBot.jobs.JobsScheduler.createJob;
import static telegramBot.jobs.JobsScheduler.createTrigger;

public class Main {

    public static void main(String[] args) {
        TelegramBotsApi telegramBotsApi;
        Bot bot = new Bot();
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        try {
            Sender.setBot(bot);
            ExceptionHandler.setBot(bot);

            JobDetail readyToReviewMr_job = createJob(ReadyToReviewMr_Ntf.class, "ReadyToReviewMr", "Notification", Property.get("ntf.enabledMainProjects"));
            Trigger readyToReviewMr_trigger = createTrigger("readyToReviewMr_trigger",
                    Property.get("ntf.periodForReview_MR_I_cron"),
                    "ReadyToReviewMr",
                    "Notification");

            JobDetail readyToReviewMr_afterWeekend_job = createJob(ReadyToReviewMr_Ntf.class, "ReadyToReviewMr_afterWeekend", "Notification",
                    "afterDate",
                    Property.get("ntf.reviewMR_afterWeekend"),
                    Property.get("ntf.enabledMainProjects"));
            Trigger readyToReviewMr_afterWeekend_trigger = createTrigger("readyToReviewMrAfterWeekend_trigger",
                    Property.get("ntf.periodForReview_afterWeekend_MR_I_cron"),
                    "ReadyToReviewMr_afterWeekend",
                    "Notification");

            new JobsScheduler()
                    .setJob(readyToReviewMr_job, readyToReviewMr_trigger)
                    .setJob(readyToReviewMr_afterWeekend_job, readyToReviewMr_afterWeekend_trigger);
        } catch (Throwable e) {
            ExceptionHandler.handleException(e);
        }
    }

}






