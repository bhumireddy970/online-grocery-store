import Alert from "./Alert";


export default {
  title: "components/Alert",
  component: Alert,
  tags: ["autodocs"],
};

export const Success={
    args:{
        isOpen:true,
        message:"Profile updated successfully",
        type:"Success",
        onClose: ()=>{}
    }
}

export const Error={
    args:{
        isOpen:true,
        message:"Something went wrongy",
        type:"Error",
        onClose: ()=>{}
    }
}
