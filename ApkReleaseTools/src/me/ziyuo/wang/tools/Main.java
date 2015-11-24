package me.ziyuo.wang.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		List<String> chanels = null;

		/**
		 * 1.��ȡ���������б� <br/>
		 * 2.Ϊÿ�������б����ɹ���Ŀ¼ <br/>
		 * 3.����Ŀ¼�н��нṹ��<br/>
		 * 4.���ɴ���������ķ��а�<br/>
		 * 5.�������ռ�
		 */
		try {
			/**
			 * ��ȡ�����б�
			 */
			chanels = readLines(CommValues.CHANELS_PATH);
			if (chanels != null && chanels.size() > 0) {
				for (String chanel : chanels) {
					// �������������ռ�
					File chanelWorkDir = new File(CommValues.WORK_PATH, chanel);
					if (!chanelWorkDir.exists()) {
						chanelWorkDir.mkdir();
					}
					/**
					 * ����Ŀ¼�ṹ��
					 */
					File apkFile = new File(chanelWorkDir, new ReleaseNameGenerator() {
						
						@Override
						public String getReleaseName(String chanelName, String apkName) {
							return chanelName + "_"
									+ CommValues.APK_LOCATION.getName();//�������ģʽ���Զ���ReleaseNameGenerator�������Զ����µķ��а�װ����
						}
					}.getReleaseName(chanel,CommValues.APK_LOCATION.getName()));
					FileUtils.copyFile(CommValues.APK_LOCATION, apkFile);// ����Դ���а�
					File keyDir = new File(chanelWorkDir, CommValues.META_INFO);
					if (!keyDir.exists())
						keyDir.mkdir();
					File chanelInfoFile = new File(keyDir, CommValues.PREFIX 
							+ chanel);
					System.out.println(chanelInfoFile.getAbsolutePath());
					chanelInfoFile.createNewFile();

					String cmd = "aapt a " + apkFile.getName() + " "
							+ CommValues.META_INFO + File.separator
							+ CommValues.PREFIX + chanel;
					System.out.println(cmd);
					Process process = execRuntime(cmd, chanelWorkDir);// ִ��aapt
					process.waitFor();// ������ǰ���̣��ȴ�Command����ִ�����
					if (process.exitValue() == 0) {
						System.out.println("����ִ����  Command ���");
					} else {
						new RuntimeException("ִ�� Command ��������쳣��");
					}
					// ִ����ɺ�Ϳ��� ����� outĿ¼����
					FileUtils.copyFileToDirectory(apkFile, CommValues.OUT_PATH);
					FileUtils.deleteDirectory(chanelWorkDir);// ������������������,ɾ�������ռ�
					System.out.println("ɾ������Ŀ¼:["
							+ chanelWorkDir.getAbsolutePath() + "]���");
				}

			} else {
				System.out.println("���޿����������������������������ļ�(chanels.txt)�ļ�");
			}

		} catch (Exception e) {// ��������Exception
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ�����ŵ��б�(������������������Դ)
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static List<String> readLines(String path) throws Exception {
		List<String> lines = new ArrayList<>();
		String line = null;
		BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
		while ((line = bufferedReader.readLine()) != null) {
			lines.add(line);
			System.out.println("line :" + line);
		}
		bufferedReader.close();
		return lines;
	}

	public static Process execRuntime(String cmd, File runInDir)
			throws IOException {
		return Runtime.getRuntime().exec(cmd, null, runInDir);
	}
}
