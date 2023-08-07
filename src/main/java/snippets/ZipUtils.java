package snippets;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

	/***
	 *
	 * @param file {@linkplain File} object of compressed file
	 * @return path of the uncompressed file
	 * @throws IOException
	 * @throws ZipException
	 */

	public static double size;
	public static AtomicInteger point;
	public static AtomicInteger fileNo;
	public static StringBuffer sb;

	public static String unZip(String src) throws ZipException, IOException {
		return unZip(src, src.replace(".zip", "-unzip"));
	}

	public static String unZip(String src, String dest) throws ZipException, IOException {
		long begin = System.currentTimeMillis();
		ZipFile zipFile = new ZipFile(new File(src));

		Path unZipPath = Path.of(dest);
		File unZipFile = new File(unZipPath.toString());
		unZipFile.mkdirs();

		size = zipFile.size();
		sb = new StringBuffer();
		point = new AtomicInteger(0);
		fileNo = new AtomicInteger(0);

		zipFile.stream().parallel().forEach(entry -> writeEntryAsFile(zipFile, entry, unZipPath));
		zipFile.close();

		long end = System.currentTimeMillis();
		System.out.println(((end - begin) / 1000f) + " seconds taken");
		return unZipFile.getPath();
	}

	private static void writeEntryAsFile(ZipFile zipFile, ZipEntry entry, Path unZipPath) {
		Path entryPath = Path.of(unZipPath.toString() + File.separator + entry.getName());
		File entryFile = entryPath.toFile();
		printState();

		if (entry.isDirectory()) {
			entryFile.mkdirs();
			return;
		}

		try {
			if (!entryFile.exists()) {
				entryFile.getParentFile().mkdirs();
				entryFile.createNewFile();
			}
			InputStream in = zipFile.getInputStream(entry);
			OutputStream out = new FileOutputStream(entryFile);
			out.write(in.readAllBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println();
			e.printStackTrace();
		}
	}

	public static String zip(String src) throws IOException {
		return zip(src, Path.of(src) + "-zip");
	}

	public static String zip(String src, String dest) throws IOException {
		long begin = System.currentTimeMillis();
		File unZipFile = new File(src);

		if (unZipFile.isFile())
			throw new RuntimeException(src + " is not a zip file");

		Path zipPath = Path.of(Path.of(dest) + ".zip");
		File zipFile = zipPath.toFile();
		zipFile.getParentFile().mkdirs();
		zipFile.createNewFile();

		List<String> entries = getEntries(unZipFile);

		size = entries.size();
		sb = new StringBuffer();
		point = new AtomicInteger(0);
		fileNo = new AtomicInteger(0);

		FileOutputStream fout = new FileOutputStream(zipFile);
		ZipOutputStream zout = new ZipOutputStream(fout);

		entries.parallelStream().forEach(entry -> writeAndZipIt(zout, entry, unZipFile.getPath() + File.separator));
		zout.close();

		long end = System.currentTimeMillis();
		System.out.println(((end - begin) / 1000f) + " seconds taken");
		return zipFile.getPath();
	}

	private static synchronized void writeAndZipIt(ZipOutputStream outputStream, String entryPath, String basePath) {
		try {
			ZipEntry entry = new ZipEntry(entryPath.replace(basePath, ""));
			if (isFile(entryPath)) {
				byte[] bytes = Files.readAllBytes(Path.of(entryPath));
				writeBytesToStream(bytes, outputStream, entry);
			}
			printState();
		} catch (IOException e) {
			System.out.println();
			e.printStackTrace();
		}
	}

	private static void writeBytesToStream(byte[] bytes, ZipOutputStream stream, ZipEntry entry) throws IOException {
		synchronized (stream) {
			stream.putNextEntry(entry);
			if (bytes != null) {
				stream.write(bytes);
				stream.flush();
			}
			stream.closeEntry();
		}
	}

	private static List<String> getEntries(File file) {
		if (file.isFile())
			return Arrays.asList(file.getAbsolutePath());
		List<String> entries = List.of(file.listFiles()).parallelStream().flatMap(f -> getEntries(f).parallelStream()).collect(Collectors.toList());
		if (entries.isEmpty())
			entries.add(file.getAbsolutePath() + File.separator);
		return entries;
	}

	public static byte[] compress(String src) {
		return compress(src.getBytes(), 9);
	}

	public static byte[] compress(String src, int level) {
		return compress(src.getBytes(), level);
	}

	public static byte[] compress(byte[] bytes, int level) {
		byte[] buf = new byte[1024];
		level = checkCompressionLevel(level);
		Deflater deflater = new Deflater(level);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		deflater.setInput(bytes);
		while (deflater.getBytesRead() < bytes.length) {
			int compSize = deflater.deflate(buf, 0, buf.length, Deflater.FULL_FLUSH);
			bos.write(buf, 0, compSize);
		}
		System.out.println("Compressed Size: " + deflater.getBytesWritten());
		deflater.end();
		return bos.toByteArray();
	}

	public static byte[] decompress(String src) throws DataFormatException {
		return decompress(src.getBytes());
	}

	public static byte[] decompress(byte[] bytes) throws DataFormatException {
		byte[] buf = new byte[1024];
		Inflater inflater = new Inflater();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		inflater.setInput(bytes);
		while (inflater.getRemaining() != 0) {
			int decomSize = inflater.inflate(buf);
			bos.write(buf, 0, decomSize);
		}
		System.out.println("Decompressed Size: " + inflater.getBytesWritten());
		inflater.end();
		return bos.toByteArray();
	}

	private static void printState() {
		int tp = fileNo.incrementAndGet();
		while (((size / 100d) * point.get()) < tp) {
			sb.append("#");
			System.out.print("\r" + sb.toString() + " (" + point.incrementAndGet() + "%)" + (point.get() >= 100 ? "\n" : ""));
		}
	}

	private static int checkCompressionLevel(int level) {
		if (level < 0)
			return 0;
		if (level > 9)
			return 9;
		return level;
	}

	private static boolean isFile(String path) {
		return !path.endsWith(File.separator);
	}
}
