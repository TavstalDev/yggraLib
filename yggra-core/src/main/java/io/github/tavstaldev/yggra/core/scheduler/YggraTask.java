package io.github.tavstaldev.yggra.core.scheduler;

public abstract class YggraTask implements Runnable, ITask {
    private io.papermc.paper.threadedregions.scheduler.ScheduledTask foliaTask;
    private org.bukkit.scheduler.BukkitTask paperTask;
    private boolean cancelled = false;

    public void setPaperTask(org.bukkit.scheduler.BukkitTask paperTask) { this.paperTask = paperTask; }
    public void setFoliaTask(io.papermc.paper.threadedregions.scheduler.ScheduledTask foliaTask) { this.foliaTask = foliaTask; }

    @Override
    public abstract void run();

    @Override
    public final void cancel() {
        this.cancelled = true;
        if (foliaTask != null) foliaTask.cancel();
        if (paperTask != null) paperTask.cancel();
    }

    @Override
    public final boolean isCancelled() {
        if (foliaTask != null)
            return foliaTask.isCancelled();

        if (paperTask != null)
            return paperTask.isCancelled();

        return cancelled;
    }
}
