
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FormatImageName {

    private static TextPinyinUtil mPinyinUtil = TextPinyinUtil.getInstance();

    public static void main(String[] args) {
        String path = "path";
        try {
            mainFormat(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 主方法，入口
     *
     * @param path
     * @throws IOException
     */
    private static void mainFormat(String path) throws IOException {
        File file = new File(path);
        File[] images = file.listFiles();
        for (File image : images) {
            String name = image.getName();
            if (image.isDirectory() || (!name.endsWith(".png") && !name.endsWith(".jpg")))
                continue;
            if (name.matches(".*(@2x|@3x|@1x)(.png|.jpg)$")) {
                initAndroidFile(path, (getImageLevel(name)));
                copyFile(image, getAndroidImageFile(path, name));
                continue;
            }
            if (name.matches(".*(.png|.jpg)$")) {
                String name1 = image.getName().split("[.]")[0];
                if (name.matches("[a-z0-9_]*"))
                    continue;
                image.renameTo(new File(path + File.separator + formatName(name1) + ".png"));
            }

        }

    }

    private static void initAndroidFile(String path, int level) {
        String resultPath = getAndroidFileName(path, level);
        File file = new File(resultPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private static String getAndroidFileName(String path, int level) {
        String resultPath = null;
        switch (level) {
            case 1:
                resultPath = path + File.separator + "drawable-xhdpi";
                break;
            case 2:
                resultPath = path + File.separator + "drawable-xxhdpi";
                break;
            case 3:
                resultPath = path + File.separator + "drawable-xxxhdpi";
                break;
            default:
                resultPath = path + File.separator + "drawable-xhdpi";
                break;
        }
        return resultPath;
    }

    /**
     * 获取android的完整文件名
     */
    private static File getAndroidImageFile(String path, String name) {
        return new File(getAndroidFileName(path, getImageLevel(name)) + File.separator + formatImageName(name));
    }

    /**
     * 将iOS的图片名字，转换成android的
     */
    private static String formatImageName(String name) {
        String name1 = name.split("[.@]")[0];
        String name2 = formatName(name1);
        return name2 + "." + name.split("[.@]")[2];
    }

    private static String formatName(String name1) {
        StringBuffer stringBuffer = new StringBuffer();
        for (char char1 : name1.toCharArray()) {
            if ((char1 > '0' && char1 < '9') || (char1 > 'a' && char1 < 'z') || char1 == '_')
                stringBuffer.append(char1);
            if (char1 > 'A' && char1 < 'Z')
                stringBuffer.append(String.valueOf(char1).toLowerCase());
            if (TextPinyinUtil.isChinaString(String.valueOf(char1)))
                stringBuffer.append(mPinyinUtil.getPinyin(String.valueOf(char1)));

        }
        if (stringBuffer.length() == 0) {
            stringBuffer.append("default");
        }
        return stringBuffer.toString();
    }

    /**
     * 获取iOS图片级别
     */
    private static int getImageLevel(String name) {
        return Integer.valueOf(name.split("[.@]")[1].substring(0, 1));
    }

    private static void copyFile(File fromFile, File toFile) throws IOException {
        FileInputStream ins = new FileInputStream(fromFile);
        FileOutputStream out = new FileOutputStream(toFile);
        byte[] b = new byte[1024];
        int n = 0;
        while ((n = ins.read(b)) != -1) {
            out.write(b, 0, n);
        }
        ins.close();
        out.close();
    }
}
