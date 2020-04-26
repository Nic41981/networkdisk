package com.dy.networkdisk.file.tool

class SizeTransUtil {

    companion object{
        private const val KB_LENGTH = 1024L
        private const val MB_LENGTH = 1024L * 1024L
        private const val GB_LENGTH = 1024L * 1024L * 1024L
        private const val TB_LENGTH = 1024L * 1024L * 1024L * 1024L

        fun trans(size: Long?): String{
            if (size == null){
                return "-"
            }
            return when(size){
                in 0 until KB_LENGTH -> "${size}B"
                in KB_LENGTH until MB_LENGTH -> "${size.toDouble() / KB_LENGTH}KB"
                in MB_LENGTH until GB_LENGTH -> "${size.toDouble() / MB_LENGTH}MB"
                in GB_LENGTH until TB_LENGTH -> "${size.toDouble() / GB_LENGTH}GB"
                else -> "${size.toDouble() / TB_LENGTH}TB"
            }
        }
    }
}
