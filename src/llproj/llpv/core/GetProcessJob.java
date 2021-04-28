package llproj.llpv.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

import llproj.llpv.util.MessageUt;
import llproj.llpv.vo.DataVO;

public class GetProcessJob {
  private static final Logger log = Logger.getLogger(GetProcessJob.class);

  public DataVO getProgram() throws Exception {
    String run_file = "";
    String run_title = "";
    String _datetime = "";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    _datetime = sdf.format(new Date());

    // finding run_file
    HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
    final int PROCESS_VM_READ = 0x0010;
    final int PROCESS_QUERY_INFORMATION = 0x0400;
    final User32 user32 = User32.INSTANCE;
    final Kernel32 kernel32 = Kernel32.INSTANCE;
    final Psapi psapi = Psapi.INSTANCE;
    WinDef.HWND windowHandle = user32.GetForegroundWindow();
    IntByReference pid = new IntByReference();
    user32.GetWindowThreadProcessId(windowHandle, pid);
    WinNT.HANDLE processHandle = kernel32.OpenProcess(1020, true, pid.getValue());

    byte[] filename = new byte[512];
    try {
      Psapi.INSTANCE.GetModuleBaseNameW(processHandle.getPointer(), Pointer.NULL, filename,
          filename.length);
    } catch (Exception e) {
      log.error(MessageUt.getMessage("error.unknown"));
    }
    String file_id = "";
    for (int i = 0; i < filename.length; i++) {
      if (filename[i] != 0)
        file_id = file_id + (char) filename[i];
    }

    run_file = !file_id.equals("") ? file_id : "Unknown File";

    // finding run_titie
    HWND fgWindow = User32.INSTANCE.GetForegroundWindow();
    int titleLength = User32.INSTANCE.GetWindowTextLength(fgWindow) + 1;
    char[] title = new char[titleLength];
    User32.INSTANCE.GetWindowText(fgWindow, title, titleLength);

    run_title = Native.toString(title);

    DataVO dv = new DataVO();
    dv.setRun_file(run_file);
    dv.setRun_title(run_title);
    dv.set_datetime(_datetime);
    dv.setPid(pid.getValue());

    return dv;

  }
}
