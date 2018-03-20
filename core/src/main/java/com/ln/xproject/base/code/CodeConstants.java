package com.ln.xproject.base.code;

/**
 * 错误码列表（注：命名以"C_"开头）
 */
public class CodeConstants {

    /** ---------- 系统 ---------- */
    public static final Code C_10101000 = new Code("10101000", "系统异常");
    public static final Code C_10101001 = new Code("10101001", "%s不能为空");
    public static final Code C_10101002 = new Code("10101002", "%s");
    public static final Code C_10101003 = new Code("10101003", "%s不能小于零");
    public static final Code C_10101004 = new Code("10101004", "%s枚举转化失败");
    public static final Code C_10101005 = new Code("10101005", "%s不存在");
    public static final Code C_10101006 = new Code("10101006", "%s不正确");
    public static final Code C_10101007 = new Code("10101007", "%s不能小于等于零");
    public static final Code C_10101008 = new Code("10101008", "%s已存在");
    public static final Code C_10101009 = new Code("10101009", "时间戳格式不对");
    public static final Code C_10101010 = new Code("10101010", "%s现在是%s状态，不能设置为%s状态");
    public static final Code C_10101011 = new Code("10101011", "请勿重复提交");
    public static final Code C_10101012 = new Code("10101012", "[%s]长度必须在[%s]到[%s]之间");
    public static final Code C_10101013 = new Code("10101013", "%s不能大于%s");
    public static final Code C_10101014 = new Code("10101014", "文件类型不正确");
    public static final Code C_10101015 = new Code("10101015", "金额精度不符合要求");
    public static final Code C_10101016 = new Code("10101016", "[%s]必须在[%s]到[%s]之间");
    public static final Code C_10101017 = new Code("10101017", "[%s]尚不支持");
    public static final Code C_10101018 = new Code("10101018", "状态[%s]不正确，不能%s");
    public static final Code C_10101019 = new Code("10101019", "文件类型不正确");
    public static final Code C_10101020 = new Code("10101020", "不应到达的处理逻辑");
    public static final Code C_10101021 = new Code("10101021", "时间格式不正确");
    public static final Code C_10101022 = new Code("10101022", "[%s]数据格式不正确");

    /** ---------- 用户/角色/权限 ---------- */
    public static final Code C_10111000 = new Code("10111000", "未登录");
    public static final Code C_10111001 = new Code("10111001", "没有权限");
    public static final Code C_10111002 = new Code("10111002", "请重新设置密码");
    public static final Code C_10111003 = new Code("10111003", "用户名密码错误");
    public static final Code C_10111004 = new Code("10111004", "用户已冻结");
    public static final Code C_10111005 = new Code("10111005", "账号已停用");
    public static final Code C_10111006 = new Code("10111006", "未知错误，登录失败");
    public static final Code C_10111007 = new Code("10111007", "没有权限");
    public static final Code C_10111008 = new Code("10111008", "会话过期");
    public static final Code C_10111009 = new Code("10111009", "账号不存在");
    public static final Code C_10111010 = new Code("10111010", "该角色已分配给用户，不能删除");
    public static final Code C_10111011 = new Code("10111011", "角色不能为空");
    public static final Code C_10111012 = new Code("10111001", "没有数据操作权限");
    public static final Code C_10111013 = new Code("10111013", "旧密码错误");

    /** ---------- 进件api ---------- */
    public static final Code C_000000 = new Code("000000", "成功");
    public static final Code C_000001 = new Code("000001", "处理中");

    public static final Code C_10121000 = new Code("10121000", "进件审核驳回");
    public static final Code C_10121001 = new Code("10121001", "进件放款失败");
    public static final Code C_10121002 = new Code("10121002", "请勿重复进件");
    public static final Code C_10121003 = new Code("10121003", "签名错误");
    public static final Code C_10121004 = new Code("10121004", "推标失败");
    public static final Code C_10121005 = new Code("10121005", "业务和审核状态不匹配");
    public static final Code C_10121006 = new Code("10121006", "获取锁失败");
}
