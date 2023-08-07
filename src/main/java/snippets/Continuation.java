package snippets;

import java.lang.Thread.State;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

class Continuation implements AutoCloseable {

	private Thread thread;
	private Queue<Object> queue;
	private AtomicBoolean locked;

	private Continuation(Consumer<Yielder> task, long maxTimeToWait, TimeUnit timeUnit) {
		Runnable taskRunner = () -> task.accept(this.new Yielder());
		this.thread = new Thread(taskRunner);
		this.queue = new LinkedList<>();
		// System.out.println("Continuation started...");
		this.locked = new AtomicBoolean(false);
	}

	public Object get() {
		locked.set(true);
		System.out.println("get State: " + thread.getState());
		if (thread.getState().equals(State.NEW)) {
			thread.start();
		} else {
			synchronized (thread) {
				thread.notify();
			}
		}

		while (locked.get()) {}
		System.out.println(thread.getState() + " ::::");
		return queue.poll();
	}

	public static Continuation submit(Consumer<Yielder> task, long maxTimeToWait, TimeUnit timeUnit) {
		Continuation continuation = new Continuation(task, maxTimeToWait, timeUnit);
		return continuation;
	}

	class Yielder {
		public <E> void yeild(E e) {
			try {
				synchronized (thread) {
					queue.add(e);
					locked.set(false);
					thread.wait();
				}
			} catch (InterruptedException ex) {
			}
		}
	}

	@Override
	public void close() {
		synchronized (thread) {
			thread.notifyAll();
		}
	}

	public static void main(String... strings) throws Exception {
		System.out.println("Hey, Dev!");

		Continuation con = Continuation.submit(yeilder -> {
			int x = 10;
			int y = 20;

			System.out.println("Here....1");
			System.out.println("---------------------------");
			yeilder.yeild(x + " " + y);

			x = 20;
			y = 30;

			System.out.println("Here....2");
			System.out.println("---------------------------");
			yeilder.yeild(x + " " + y);
		}, 1, TimeUnit.MINUTES);

		System.out.println("test1 :: " + con.get());
//
//		System.out.println("test2 :: " + con.get());
//
//		System.out.println("test3 :: " + con.get());

		con.close();

		System.out.println("AFTER;;;;;;;;;;;;;" + con.get());

		System.out.println("=============================");

		long time = 10;

	}
}
